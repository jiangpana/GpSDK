package com.rj.core.huawei.pay;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.util.Log;

import com.google.gson.Gson;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.iap.Iap;
import com.huawei.hms.iap.IapApiException;
import com.huawei.hms.iap.IapClient;
import com.huawei.hms.iap.entity.ConsumeOwnedPurchaseReq;
import com.huawei.hms.iap.entity.ConsumeOwnedPurchaseResult;
import com.huawei.hms.iap.entity.InAppPurchaseData;
import com.huawei.hms.iap.entity.OrderStatusCode;
import com.huawei.hms.iap.entity.OwnedPurchasesReq;
import com.huawei.hms.iap.entity.OwnedPurchasesResult;
import com.huawei.hms.iap.entity.ProductInfoReq;
import com.huawei.hms.iap.entity.ProductInfoResult;
import com.huawei.hms.iap.entity.PurchaseIntentReq;
import com.huawei.hms.iap.entity.PurchaseIntentResult;
import com.huawei.hms.iap.entity.PurchaseResultInfo;
import com.huawei.hms.support.api.client.Status;
import com.rj.core.huawei.ui.PayTransparentActivity;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.rj.core.utils.UnityUtil.callUnity;
import static com.rj.core.utils.UnityUtil.getActivity;


public class HwPayHelper {


    private static String TAG = "PayHelper";


    /**
     * 解除订阅
     */
    public void unSubscribe() {
        OwnedPurchasesReq ownedPurchasesReq = new OwnedPurchasesReq();
        // priceType: 0: consumable; 1: non-consumable; 2: auto-renewable subscription
        ownedPurchasesReq.setPriceType(2);
        Task<OwnedPurchasesResult> task = Iap.getIapClient(getActivity()).obtainOwnedPurchases(ownedPurchasesReq);
        task.addOnSuccessListener(result -> {
            // Obtain the execution result.
            if (result != null && result.getInAppPurchaseDataList() != null) {
                for (int i = 0; i < result.getInAppPurchaseDataList().size(); i++) {
                    String inAppPurchaseData = result.getInAppPurchaseDataList().get(i);
                    String InAppSignature = result.getInAppSignature().get(i);
                    try {
                        InAppPurchaseData inAppPurchaseDataBean = new InAppPurchaseData(inAppPurchaseData);
                        int purchaseState = inAppPurchaseDataBean.getPurchaseState();
                        Log.e(TAG, "订阅id " + inAppPurchaseDataBean.getSubscriptionId());
                        Log.e(TAG, "订阅的token " + inAppPurchaseDataBean.getPurchaseToken());
                        post(inAppPurchaseDataBean);
                    } catch (JSONException e) {

                    }
                }

            }
        });
    }

    private void post(final InAppPurchaseData inAppPurchaseDataBean) {
        new Thread(() -> {
            String appAt = null;
            try {
                appAt = HwAtDemo.getAppAT();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // construct the Authorization in Header
            Map<String, String> headers = HwAtDemo.buildAuthorization(appAt);
            // pack the request body
            Map<String, String> bodyMap = new HashMap<>();
            bodyMap.put("subscriptionId", inAppPurchaseDataBean.getSubscriptionId());
            bodyMap.put("purchaseToken", inAppPurchaseDataBean.getPurchaseToken());
            String msgBody = new Gson().toJson(bodyMap);
            String response = null;
            try {
                response = HwAtDemo.httpPost("https://subscr-drcn.iap.hicloud.com" + "/sub/applications/v2/purchases/stop",
                        "application/json; charset=UTF-8", msgBody, 5000, 5000, headers);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // TODO: display the response as string in console, you can replace it with your business logic.
            if (response != null) {
                returnFee(inAppPurchaseDataBean);
            }
        }).start();
    }

    private void returnFee(InAppPurchaseData inAppPurchaseDataBean) {
        new Thread(() -> {
            String appAt = null;
            try {
                appAt = HwAtDemo.getAppAT();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Map<String, String> headers = HwAtDemo.buildAuthorization(appAt);
            // pack the request body
            Map<String, String> bodyMap = new HashMap<>();
            bodyMap.put("subscriptionId", inAppPurchaseDataBean.getSubscriptionId());
            bodyMap.put("purchaseToken", inAppPurchaseDataBean.getPurchaseToken());
            String msgBody = new Gson().toJson(bodyMap);
            try {
                String response = HwAtDemo.httpPost("https://subscr-drcn.iap.hicloud.com" + "/sub/applications/v2/purchases/returnFee",
                        "application/json; charset=UTF-8", msgBody, 5000, 5000, headers);
                System.out.println("TAG -> " + response);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();
    }

    /**
     * 查询商品
     */
    public void queryGoods(String id) {
        IapClient iapClient = Iap.getIapClient(getActivity());
        Task<ProductInfoResult> task = iapClient.obtainProductInfo(createProductInfoReq(id));
        task.addOnSuccessListener(result -> {
            if (result != null && !result.getProductInfoList().isEmpty()) {
                //查询成功 result.getProductInfoList()
                callUnity("androidCallBack", "GetPruductInfo", new Gson().toJson(result.getProductInfoList()));

            }
        }).addOnFailureListener(e -> Log.e(TAG, "queryGoods: "+ e.toString() ));


    }

    /**
     * 查询订阅状态
     * id ->  不为0 都是没有订阅
     */
    public void querySubscribe() {
        OwnedPurchasesReq ownedPurchasesReq = new OwnedPurchasesReq();
        // priceType: 0: consumable; 1: non-consumable; 2: auto-renewable subscription
        ownedPurchasesReq.setPriceType(2);
        Task<OwnedPurchasesResult> task = Iap.getIapClient(getActivity()).obtainOwnedPurchases(ownedPurchasesReq);
        task.addOnSuccessListener(result -> {
            // Obtain the execution result.
            if (result != null && result.getInAppPurchaseDataList() != null) {
                List<GoodBean> goodBeans =new ArrayList<>();
                for (int i = 0; i < result.getInAppPurchaseDataList().size(); i++) {
                    String inAppPurchaseData = result.getInAppPurchaseDataList().get(i);
                    try {
                        InAppPurchaseData inAppPurchaseDataBean = new InAppPurchaseData(inAppPurchaseData);
                        GoodBean bean =new GoodBean();
                        bean.setProductId(inAppPurchaseDataBean.getProductId());
                        bean.setProductPrice(inAppPurchaseDataBean.getPrice());
                        bean.setCurrency(inAppPurchaseDataBean.getCurrency());
                        bean.setSubscriptionStatus( inAppPurchaseDataBean.getPurchaseState());
                        goodBeans.add(bean);
                    } catch (JSONException e) {

                    }
                }
                callUnity("androidCallBack", "GetSubscriptionInfo", new Gson().toJson(goodBeans));
            }
        });
    }


    /**
     * 去支付
     * type  0 为内购消费 1为内购不消费 2 为 订阅
     */

    public void gotoPay(String productId) {
        Intent intent = new Intent(getActivity(), PayTransparentActivity.class);
        id = productId;
        getActivity().startActivity(new Intent(getActivity(), PayTransparentActivity.class));

//        IapClient mClient = Iap.getIapClient(getActivity());
//        Task<PurchaseIntentResult> task = mClient.createPurchaseIntent(createPurchaseIntentReq(2, productId));
//        task.addOnSuccessListener(result -> onPaySuccess(result, getActivity())).addOnFailureListener(e -> onPayFailure(e, getActivity()));

    }

    public static String id;

    public static void pay(final Activity activity) {
        IapClient mClient = Iap.getIapClient(activity);
        Task<PurchaseIntentResult> task = mClient.createPurchaseIntent(createPurchaseIntentReq(2, id));
        task.addOnSuccessListener(result -> onPaySuccess(result, activity)).addOnFailureListener(e -> onPayFailure(e, activity));
    }


    private static ProductInfoReq createProductInfoReq(String id) {
        ProductInfoReq req = new ProductInfoReq();
        req.setPriceType(IapClient.PriceType.IN_APP_SUBSCRIPTION);
        ArrayList<String> productIds = new ArrayList<>();
        productIds.add(id);
        req.setProductIds(productIds);
        return req;
    }


    private static void onPayFailure(Exception e, Activity activity) {
        Log.e(TAG, Objects.requireNonNull(e.getMessage()));
        if (e instanceof IapApiException) {
            IapApiException apiException = (IapApiException) e;
            int returnCode = apiException.getStatusCode();
            Log.e(TAG, "createPurchaseIntent, returnCode: " + returnCode);

        }
    }

    private static void onPaySuccess(PurchaseIntentResult result, Activity activity) {
        Log.i(TAG, "createPurchaseIntent, onSuccess");
        if (result == null) {
            Log.e(TAG, "result is null");
            return;
        }
        Status status = result.getStatus();
        if (status == null) {
            Log.e(TAG, "status is null");
            return;
        }
        if (status.hasResolution()) {
            try {
                status.startResolutionForResult(activity, REQ_CODE_BUY);
            } catch (IntentSender.SendIntentException exp) {
                Log.e(TAG, exp.getMessage());
            }
        }
    }

    private static PurchaseIntentReq createPurchaseIntentReq(int type, String productId) {
        PurchaseIntentReq req = new PurchaseIntentReq();
        req.setProductId(productId);
        req.setPriceType(type);
        req.setDeveloperPayload("test");
        return req;
    }

    private static int REQ_CODE_BUY = 0x11;

    /**
     * 消费商品
     */
    private void consumeOwnedPurchase(final Context context, String inAppPurchaseData) {
        IapClient mClient = Iap.getIapClient(context);
        Task<ConsumeOwnedPurchaseResult> task = mClient.consumeOwnedPurchase(createConsumeOwnedPurchaseReq(inAppPurchaseData));
        task.addOnSuccessListener(new OnSuccessListener<ConsumeOwnedPurchaseResult>() {
            @Override
            public void onSuccess(ConsumeOwnedPurchaseResult result) {
                // Consume success
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, e.getMessage());
            if (e instanceof IapApiException) {
                IapApiException apiException = (IapApiException) e;
                Status status = apiException.getStatus();
                int returnCode = apiException.getStatusCode();
                Log.e(TAG, "consumeOwnedPurchase fail,returnCode: " + returnCode);
            }
        });
    }

    private ConsumeOwnedPurchaseReq createConsumeOwnedPurchaseReq(String purchaseData) {
        ConsumeOwnedPurchaseReq req = new ConsumeOwnedPurchaseReq();
        // Parse purchaseToken from InAppPurchaseData in JSON format.
        try {
            InAppPurchaseData inAppPurchaseData = new InAppPurchaseData(purchaseData);
            req.setPurchaseToken(inAppPurchaseData.getPurchaseToken());
        } catch (JSONException e) {
            Log.e(TAG, "createConsumeOwnedPurchaseReq JSONExeption");
        }

        return req;
    }

    //成功0 失败1
    public static void onPayResult(Activity activity, int requestCode, Intent data) {
        if (requestCode == REQ_CODE_BUY) {
            PurchaseResultInfo purchaseResultInfo = Iap.getIapClient(activity).parsePurchaseResultInfoFromIntent(data);
            if (purchaseResultInfo.getReturnCode() == OrderStatusCode.ORDER_STATE_SUCCESS) {
                callUnity("androidCallBack", "GetPayState", "0");
            } else {
                callUnity("androidCallBack", "GetPayState", "1");
            }
        }
    }
}
