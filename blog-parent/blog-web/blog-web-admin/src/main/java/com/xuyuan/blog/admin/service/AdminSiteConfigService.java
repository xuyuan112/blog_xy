package com.xuyuan.blog.admin.service;

import java.util.Map;

public interface AdminSiteConfigService {
    Map<String, String> getAll();
    void update(Map<String, String> configs);
}
