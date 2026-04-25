package com.boot.vuevbenadminboot.web;

import com.boot.vuevbenadminboot.service.MallSkuService;
import com.boot.vuevbenadminboot.web.dto.StockOperateRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/mall/stock")
public class StockController {
    private final MallSkuService skuService;

    public StockController(MallSkuService skuService) {
        this.skuService = skuService;
    }

    @GetMapping("/list")
    public Map<String, Object> list(@RequestParam(required = false) String keyword) {
        return ApiResponse.of(0, skuService.listForManage(keyword), "success");
    }

    @PostMapping("/increase")
    public Map<String, Object> increase(@RequestBody StockOperateRequest req) {
        try {
            skuService.increaseStock(req.getSkuId(), req.getQuantity());
            return ApiResponse.of(0, null, "success");
        } catch (IllegalArgumentException e) {
            return ApiResponse.of(1, null, e.getMessage());
        }
    }

    @PostMapping("/decrease")
    public Map<String, Object> decrease(@RequestBody StockOperateRequest req) {
        try {
            skuService.decreaseStock(req.getSkuId(), req.getQuantity());
            return ApiResponse.of(0, null, "success");
        } catch (IllegalArgumentException e) {
            return ApiResponse.of(1, null, e.getMessage());
        }
    }

    @PostMapping("/lock")
    public Map<String, Object> lock(@RequestBody StockOperateRequest req) {
        try {
            skuService.lockStock(req.getSkuId(), req.getQuantity());
            return ApiResponse.of(0, null, "success");
        } catch (IllegalArgumentException e) {
            return ApiResponse.of(1, null, e.getMessage());
        }
    }

    @PostMapping("/unlock")
    public Map<String, Object> unlock(@RequestBody StockOperateRequest req) {
        try {
            skuService.unlockStock(req.getSkuId(), req.getQuantity());
            return ApiResponse.of(0, null, "success");
        } catch (IllegalArgumentException e) {
            return ApiResponse.of(1, null, e.getMessage());
        }
    }
}
