package com.xuyuan.blog.web.service.like.impl;

import com.xuyuan.blog.model.entity.Article;
import com.xuyuan.blog.model.entity.Like;
import com.xuyuan.blog.model.entity.Moment;
import com.xuyuan.blog.common.constant.RedisConstant;
import com.xuyuan.blog.web.dto.LikeToggleParam;
import com.xuyuan.blog.web.mapper.ArticleMapper;
import com.xuyuan.blog.web.mapper.LikeMapper;
import com.xuyuan.blog.web.mapper.MomentMapper;
import com.xuyuan.blog.web.service.like.LikeService;
import com.xuyuan.blog.web.vo.comment.LikeToggleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.redis.core.StringRedisTemplate;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeMapper likeMapper;
    private final ArticleMapper articleMapper;
    private final MomentMapper momentMapper;
    private final StringRedisTemplate redisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LikeToggleVO toggle(LikeToggleParam param, String clientIp) {
        validateTarget(param);
        String ip = clientIp == null || clientIp.isBlank() ? "unknown" : clientIp;
        String lockKey = RedisConstant.APP_LIKE_LOCK_PREFIX
                + param.getTargetType() + ":" + param.getTargetId() + ":" + ip;
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(
                lockKey, "1", RedisConstant.APP_LIKE_LOCK_TTL_SEC, TimeUnit.SECONDS);
        if (Boolean.FALSE.equals(locked)) {
            throw new IllegalArgumentException("点赞操作过于频繁，请稍后再试");
        }
        boolean liked = likeMapper.countByClient(param.getTargetType(), param.getTargetId(), ip) > 0;

        if (liked) {
            likeMapper.deleteByClient(param.getTargetType(), param.getTargetId(), ip);
        } else {
            Like like = new Like();
            like.setTargetType(param.getTargetType());
            like.setTargetId(param.getTargetId());
            like.setIp(ip);
            likeMapper.insert(like);
        }

        long likeCount = likeMapper.countByTarget(param.getTargetType(), param.getTargetId());
        synchronizeTargetCount(param, likeCount);

        LikeToggleVO result = new LikeToggleVO();
        result.setTargetId(param.getTargetId());
        result.setLiked(!liked);
        result.setLikeCount(likeCount);
        return result;
    }

    private void synchronizeTargetCount(LikeToggleParam param, long likeCount) {
        if (param.getTargetType() == 1) {
            articleMapper.setLikeCount(param.getTargetId(), likeCount);
        } else {
            momentMapper.setLikeCount(param.getTargetId(), likeCount);
        }
    }

    private void validateTarget(LikeToggleParam param) {
        if (param.getTargetType() == null || (param.getTargetType() != 1 && param.getTargetType() != 2)) {
            throw new IllegalArgumentException("目标类型必须为 1（文章）或 2（说说）");
        }
        if (param.getTargetId() == null || param.getTargetId() <= 0) {
            throw new IllegalArgumentException("目标 ID 无效");
        }
        if (param.getTargetType() == 1) {
            Article article = articleMapper.selectById(param.getTargetId());
            if (article == null || !Integer.valueOf(1).equals(article.getStatus())
                    || Integer.valueOf(1).equals(article.getDeleted())) {
                throw new IllegalArgumentException("文章不存在或未发布");
            }
        } else {
            Moment moment = momentMapper.selectById(param.getTargetId());
            if (moment == null || !Integer.valueOf(0).equals(moment.getVisible())
                    || Integer.valueOf(1).equals(moment.getDeleted())) {
                throw new IllegalArgumentException("说说不存在或不可见");
            }
        }
    }
}
