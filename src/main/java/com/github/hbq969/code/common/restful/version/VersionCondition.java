package com.github.hbq969.code.common.restful.version;

import com.google.common.base.Splitter;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.mvc.condition.RequestCondition;

/**
 * @author hbq
 */
@Slf4j
@Data
public class VersionCondition implements RequestCondition<VersionCondition> {

    private final static Pattern VERSION_PREFIX_PATTERN = Pattern.compile("v[0-9]+\\.[0-9]+");

    private final static int MAX_BINARY_LENGTH = Integer.toBinaryString(0xFFFF).length();

    public static final String PREFIX_VERSION = "v";

    public static final int VERSION_SEG = 3;

    private String apiVersion;

    public VersionCondition(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    @Override
    public VersionCondition combine(VersionCondition vc) {
        return new VersionCondition(vc.getApiVersion());
    }

    @Nullable
    @Override
    public VersionCondition getMatchingCondition(HttpServletRequest httpServletRequest) {
        Matcher matcher = VERSION_PREFIX_PATTERN.matcher(httpServletRequest.getRequestURI());
        if (matcher.find()) {
            String reqVersion = matcher.group();
            if (reqVersion.equals(getApiVersion())) {
                return this;
            }
        }
        return null;
    }

    @Override
    public int compareTo(VersionCondition other, HttpServletRequest httpServletRequest) {
        Matcher matcher = VERSION_PREFIX_PATTERN.matcher(httpServletRequest.getRequestURI());
        if (matcher.find()) {
            return toBinaryString(other.getApiVersion()).compareTo(toBinaryString(matcher.group()));
        }
        return -1;
    }

    public static String toBinaryString(String ver) {
        if (!ver.startsWith(PREFIX_VERSION)) {
            return ver;
        }
        ver = ver.substring(1);
        List<String> vl = Splitter.on('.').splitToList(ver);
        if (vl.size() != VERSION_SEG) {
            return ver;
        }
        int major = NumberUtils.toInt(vl.get(0));
        int minor = NumberUtils.toInt(vl.get(1));
        ver = PREFIX_VERSION + fillZero(major) + fillZero(minor);
        return ver;
    }

    private static String fillZero(int v) {
        String vBinary = Integer.toBinaryString(v);
        int fillVerLength = MAX_BINARY_LENGTH - vBinary.length();
        StringBuilder sb = new StringBuilder(fillVerLength);
        for (int i = 0; i < fillVerLength; i++) {
            sb.append("0");
        }
        vBinary = sb.toString() + vBinary;
        return vBinary;
    }
}
