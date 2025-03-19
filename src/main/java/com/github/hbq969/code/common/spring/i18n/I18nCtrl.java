package com.github.hbq969.code.common.spring.i18n;

import com.github.hbq969.code.common.restful.ICommonControl;
import com.github.hbq969.code.common.restful.ReturnMessage;
import com.github.hbq969.code.common.spring.context.SpringContext;
import com.github.hbq969.code.common.utils.GsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
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
            log.debug("设置国际化语言: {}", lang);
            LangInfo langInfo = new LangInfo(app, lang);
            log.debug("发布语言变化事件: {}", GsonUtils.toJson(langInfo));
            eventPublisher.publishEvent(new LanguageEvent(langInfo));
        }
        return ReturnMessage.success("设置成功");
    }

    @ApiOperation("获取语言")
    @RequestMapping(path = "/lang", method = RequestMethod.GET)
    @ResponseBody
    public ReturnMessage<String> getLang() {
        String language = Locale.getDefault().getLanguage();
        String country = Locale.getDefault().getCountry();
        return ReturnMessage.success(StringUtils.isEmpty(country)
                ? language : String.join("-", language, country));
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.eventPublisher = applicationEventPublisher;
    }
}
