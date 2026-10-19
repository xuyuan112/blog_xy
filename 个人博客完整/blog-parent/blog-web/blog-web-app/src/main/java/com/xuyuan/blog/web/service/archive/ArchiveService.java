package com.xuyuan.blog.web.service.archive;
import com.xuyuan.blog.web.vo.misc.CategoryVO;
import com.xuyuan.blog.web.vo.misc.TagVO;
import com.xuyuan.blog.web.vo.timeline.TimelineYearVO;
import java.util.List;
public interface ArchiveService { List<CategoryVO> categories(); List<TagVO> tags(); List<TimelineYearVO> timeline(); }
