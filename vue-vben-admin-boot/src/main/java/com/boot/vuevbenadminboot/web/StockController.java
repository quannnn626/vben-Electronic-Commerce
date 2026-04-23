package com.boot.vuevbenadminboot.web;

import com.boot.vuevbenadminboot.service.MallProductStockService;
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
    private final MallProductStockService productStockService;

    public StockController(MallProductStockService productStockService) {
        this.productStockService = productStockService;
    }

    @GetMapping("/list")
    public Map<String, Object> list(@RequestParam(required = false) String keyword) {
        return ApiResponse.of(0, productStockService.listForManage(keyword), "success");
    }

    @PostMapping("/increase")
    public Map<String, Object> increase(@RequestBody StockOperateRequest req) {
        try {
            productStockService.increaseStock(req.getProductId(), req.getQuantity());
            return ApiResponse.of(0, true, "success");
        } catch (IllegalArgumentException e) {
            return ApiResponse.of(1, null, e.getMessage());
        }
    }

    @PostMapping("/decrease")
    public Map<String, Object> decrease(@RequestBody StockOperateRequest req) {
        try {
            productStockService.decreaseStock(req.getProductId(), req.getQuantity());
            return ApiResponse.of(0, true, "success");
        } catch (IllegalArgumentException e) {
            return ApiResponse.of(1, null, e.getMessage());
        }
    }

    @PostMapping("/lock")
    public Map<String, Object> lock(@RequestBody StockOperateRequest req) {
        try {
            productStockService.lockStock(req.getProductId(), req.getQuantity());
            return ApiResponse.of(0, true, "success");
        } catch (IllegalArgumentException e) {
            return ApiResponse.of(1, null, e.getMessage());
        }
    }

    @PostMapping("/unlock")
    public Map<String, Object> unlock(@RequestBody StockOperateRequest req) {
        try {
            productStockService.unlockStock(req.getProductId(), req.getQuantity());
            return ApiResponse.of(0, true, "success");
        } catch (IllegalArgumentException e) {
            return ApiResponse.of(1, null, e.getMessage());
        }
    }
}
