package cn.opentp.server.network.restful.endpoint;

import cn.opentp.core.util.JacksonUtil;
import cn.opentp.server.constant.OpentpServerConstant;
import cn.opentp.server.network.restful.annotation.*;
import cn.opentp.server.network.restful.dto.BaseRes;
import cn.opentp.server.network.restful.dto.BaseResCode;
import cn.opentp.server.network.restful.dto.res.AppCreateRes;
import cn.opentp.server.network.restful.exception.RESTfulException;
import cn.opentp.server.rocksdb.OpentpRocksDB;
import cn.opentp.server.secret.MD5Util;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 用户应用信息
 */
@RestController
@RequestMapping("/apps")
public class AppEndpoint {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @PostMapping("create")
    public BaseRes<AppCreateRes> createApp(@RequestBody String requestJson) {
        String loginUser = OpentpServerConstant.DEFAULT_USER;

        OpentpRocksDB opentpRocksDB = OpentpRocksDB.rocksDB();
        String apps = opentpRocksDB.get(loginUser);
        List<String> appList = null;
        if (apps == null || apps.isEmpty()) {
            appList = new ArrayList<>();
        } else {
            appList = JacksonUtil.parseJsonList(apps, String.class);
        }

        JsonNode node = JacksonUtil.getNode(requestJson);
        String appKey = node.get("appKey").asText();
        if (appList.contains(appKey)) {
            throw new RESTfulException(BaseResCode.FAIL.getCode(), "appKey 已存在");
        }

        String appSecret = MD5Util.md5(appKey);
        log.info("user : {}, create appKey:{}, appSecret:{}", loginUser, appKey, appSecret);

        // 存储到数据库中
        appList.add(appKey);
        opentpRocksDB.set(loginUser, JacksonUtil.toJSONString(appList));
        opentpRocksDB.set(appKey, appSecret);

        AppCreateRes appCreateRes = new AppCreateRes(appKey, appSecret);
        return BaseRes.success(appCreateRes);
    }

    @GetMapping("")
    public BaseRes<List<String>> apps() {
        String loginUser = OpentpServerConstant.DEFAULT_USER;

        OpentpRocksDB opentpRocksDB = OpentpRocksDB.rocksDB();
        String apps = opentpRocksDB.get(loginUser);
        if (apps == null || apps.isEmpty()) {
            return BaseRes.success(Collections.emptyList());
        } else {
            return BaseRes.success(JacksonUtil.parseJsonList(apps, String.class));
        }
    }

    @DeleteMapping("{appKey}")
    public BaseRes<Void> apps(@PathVariable String appKey) {
        String loginUser = OpentpServerConstant.DEFAULT_USER;

        OpentpRocksDB opentpRocksDB = OpentpRocksDB.rocksDB();
        String apps = opentpRocksDB.get(loginUser);
        if (apps == null || apps.isEmpty()) {
            throw new RESTfulException(BaseResCode.NOT_ACCEPTABLE, "不存在【" + appKey + "】");
        } else {
            List<String> appKeys = JacksonUtil.parseJsonList(apps, String.class);
            if (!appKeys.contains(appKey)) {
                throw new RESTfulException(BaseResCode.NOT_ACCEPTABLE, "不存在【" + appKey + "】");
            }
            appKeys.remove(appKey);
            opentpRocksDB.set(loginUser, JacksonUtil.toJSONString(appKeys));
            opentpRocksDB.delete(appKey);
        }
        return BaseRes.success();
    }

}
