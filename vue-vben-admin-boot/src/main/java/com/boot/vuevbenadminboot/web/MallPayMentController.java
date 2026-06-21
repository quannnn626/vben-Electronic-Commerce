package com.boot.vuevbenadminboot.web;

import com.boot.vuevbenadminboot.auth.AuthConstants;
import com.boot.vuevbenadminboot.domain.MallPayment;
import com.boot.vuevbenadminboot.service.MallPaymentService;
import com.boot.vuevbenadminboot.web.dto.req.PaymentCallbackRequest;
import com.boot.vuevbenadminboot.web.dto.req.PaymentCreateRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/mall/payment")
public class MallPayMentController {

    private final MallPaymentService mallPaymentService;

    public MallPayMentController(MallPaymentService mallPaymentService) {
        this.mallPaymentService = mallPaymentService;
    }

    @PostMapping("/create")
    public Map<String, Object> createPayment(@RequestBody PaymentCreateRequest req,
                                             HttpServletRequest request) {
        try {
            String username = (String) request.getAttribute(AuthConstants.REQUEST_USERNAME);
            if (username == null) {
                return ApiResponse.of(-1, null, "未登录");
            }
            MallPayment mallPayment = mallPaymentService.createPayment(username, req);
            return ApiResponse.of(0, mallPayment, "支付单创建成功");
        } catch (IllegalArgumentException e) {
            return ApiResponse.of(1, null, e.getMessage());
        }
    }

    @PostMapping("/paymentCallback")
    public Map<String, Object> paymentCallback(@RequestBody PaymentCallbackRequest req) {
        try {
            MallPayment result = mallPaymentService.paymentCallback(req);
            return ApiResponse.of(0, result, "paySuccess");
        } catch (IllegalArgumentException e) {
            return ApiResponse.of(1, null, e.getMessage());
        }
    }
}
