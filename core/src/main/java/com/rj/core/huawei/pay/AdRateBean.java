package com.rj.core.huawei.pay;

import java.util.List;

/**
 * author: jansir
 * e-mail: 369394014@qq.com
 * date: 2020/7/1.
 */
public class AdRateBean {


    /**
     * show_interstitial_after_play : 3
     * show_interstitial : true
     * show_banner : true
     * ad_resume : false
     * ad_resume_interval : 15
     * ad_req_failure_max : 10
     * ad_strategy : [{"ad_type":"facebook","bannerRate":10,"interstitialRate":10},{"ad_type":"admob","bannerRate":90,"interstitialRate":90}]
     * ad_break_type : ["facebook"]
     * ad_break_time : 1
     * ad_break : true
     */

    private int show_interstitial_after_play;
    private boolean show_interstitial;
    private boolean show_banner;
    private boolean ad_resume;
    private int ad_resume_interval;
    private int ad_req_failure_max;
    private int ad_break_time;
    private boolean ad_break;
    private List<AdStrategyBean> ad_strategy;
    private List<String> ad_break_type;

    public int getShow_interstitial_after_play() {
        return show_interstitial_after_play;
    }

    public void setShow_interstitial_after_play(int show_interstitial_after_play) {
        this.show_interstitial_after_play = show_interstitial_after_play;
    }

    public boolean isShow_interstitial() {
        return show_interstitial;
    }

    public void setShow_interstitial(boolean show_interstitial) {
        this.show_interstitial = show_interstitial;
    }

    public boolean isShow_banner() {
        return show_banner;
    }

    public void setShow_banner(boolean show_banner) {
        this.show_banner = show_banner;
    }

    public boolean isAd_resume() {
        return ad_resume;
    }

    public void setAd_resume(boolean ad_resume) {
        this.ad_resume = ad_resume;
    }

    public int getAd_resume_interval() {
        return ad_resume_interval;
    }

    public void setAd_resume_interval(int ad_resume_interval) {
        this.ad_resume_interval = ad_resume_interval;
    }

    public int getAd_req_failure_max() {
        return ad_req_failure_max;
    }

    public void setAd_req_failure_max(int ad_req_failure_max) {
        this.ad_req_failure_max = ad_req_failure_max;
    }

    public int getAd_break_time() {
        return ad_break_time;
    }

    public void setAd_break_time(int ad_break_time) {
        this.ad_break_time = ad_break_time;
    }

    public boolean isAd_break() {
        return ad_break;
    }

    public void setAd_break(boolean ad_break) {
        this.ad_break = ad_break;
    }

    public List<AdStrategyBean> getAd_strategy() {
        return ad_strategy;
    }

    public void setAd_strategy(List<AdStrategyBean> ad_strategy) {
        this.ad_strategy = ad_strategy;
    }

    public List<String> getAd_break_type() {
        return ad_break_type;
    }

    public void setAd_break_type(List<String> ad_break_type) {
        this.ad_break_type = ad_break_type;
    }

    public static class AdStrategyBean {
        /**
         * ad_type : facebook
         * bannerRate : 10
         * interstitialRate : 10
         */

        private String ad_type;
        private int bannerRate;
        private int interstitialRate;

        public String getAd_type() {
            return ad_type;
        }

        public void setAd_type(String ad_type) {
            this.ad_type = ad_type;
        }

        public int getBannerRate() {
            return bannerRate;
        }

        public void setBannerRate(int bannerRate) {
            this.bannerRate = bannerRate;
        }

        public int getInterstitialRate() {
            return interstitialRate;
        }

        public void setInterstitialRate(int interstitialRate) {
            this.interstitialRate = interstitialRate;
        }
    }
}
