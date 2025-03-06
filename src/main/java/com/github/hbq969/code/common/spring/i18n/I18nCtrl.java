package com.github.hbq969.code.common.spring.i18n;

import com.github.hbq969.code.common.restful.ICommonControl;
import com.github.hbq969.code.common.restful.ReturnMessage;
import com.github.hbq969.code.common.spring.context.SpringContext;
import com.github.hbq969.code.common.utils.GsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Api(description = "国际化管理接口", tags = "维护使用-国际化")
@RequestMapping(path = "/hbq969-common/i18n")
@Slf4j
public class I18nCtrl implements ICommonControl, ApplicationEventPublisherAware {

    @Autowired
    private SpringContext context;

    @Value("${spring.application.name}")
    private String app;

    private ApplicationEventPublisher eventPublisher;


    @ApiOperation("设置语言")
    @RequestMapping(path = "/lang", method = RequestMethod.PUT)
    @ResponseBody
    public ReturnMessage<String> langSet(@RequestBody Map map) {
        String lang = MapUtils.getString(map, "lang");
        if (lang != null) {
            String[] parts = lang.split("[-_]");
            Locale locale = new Locale(parts[0], parts.length > 1 ? parts[1] : "");
            Locale.setDefault(locale);
            log.info("设置国际化语言: {}", lang);
            LangInfo langInfo = new LangInfo(app, lang);
            log.info("发布语言变化事件: {}", GsonUtils.toJson(langInfo));
            Map<String, LanguageChangeListener> beans = context.getBeanMapOfType(LanguageChangeListener.class);
            if (MapUtils.isNotEmpty(beans)) {
                List<LanguageChangeListener> listeners = beans.values().stream()
                        .sorted(Comparator.comparingInt(LanguageChangeListener::listenerOrder))
                        .collect(Collectors.toList());
                for (LanguageChangeListener listener : listeners) {
                    try {
                        listener.onChange(langInfo);
                    } catch (Exception e) {
                        log.error(String.format("语言变化通知监听者 [%s] 异常", listener.listenerName()), e);
                    }
                }
            }
        }
        return ReturnMessage.success("设置成功");
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.eventPublisher = applicationEventPublisher;
    }
}
