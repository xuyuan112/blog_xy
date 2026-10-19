package com.xuyuan.blog.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuyuan.blog.admin.mapper.SiteConfigMapper;
import com.xuyuan.blog.admin.service.AdminSiteConfigService;
import com.xuyuan.blog.model.entity.SiteConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminSiteConfigServiceImpl implements AdminSiteConfigService {

    private final SiteConfigMapper siteConfigMapper;

    @Override
    public Map<String, String> getAll() {
        Map<String, String> result = new LinkedHashMap<>();
        siteConfigMapper.selectList(new LambdaQueryWrapper<SiteConfig>().orderByAsc(SiteConfig::getId))
                .forEach(config -> result.put(config.getConfigKey(), config.getConfigValue()));
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Map<String, String> configs) {
        if (configs == null || configs.isEmpty()) {
            return;
        }
        configs.forEach((key, value) -> {
            if (key == null || key.isBlank()) {
                return;
            }
            SiteConfig config = siteConfigMapper.selectOne(
                    new LambdaQueryWrapper<SiteConfig>().eq(SiteConfig::getConfigKey, key));
            if (config == null) {
                config = new SiteConfig();
                config.setConfigKey(key);
                config.setConfigValue(value);
                siteConfigMapper.insert(config);
            } else {
                config.setConfigValue(value);
                siteConfigMapper.updateById(config);
            }
        });
    }
}
