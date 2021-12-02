package com.tuya.smart.srsdk.demo.config;

/**
 * @description:
 * @author: mengzi.deng
 * @since: 2021/11/12 15:04
 */
public class ApiConfig {
    /**
     * 环境
     */
    public enum EnvConfig {
        ONLINE, PREVIEW, DAILY
    }

    /**
     * 环境
     */
    private EnvConfig mEnv;

    public ApiConfig(EnvConfig env) {
        this.mEnv = env;
    }

    public EnvConfig getEnv() {
        return mEnv;
    }
}
