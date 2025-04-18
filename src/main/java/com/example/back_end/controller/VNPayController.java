package com.example.back_end.controller;

import com.example.back_end.model.entity.OrderParent;
import com.example.back_end.repository.OrderParentRepository;
import com.example.back_end.service.impl.VNPAYService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/v1/payment")
@Slf4j
public class VNPayController {
    @Autowired
    private VNPAYService vnPayService;

    @Autowired
    private OrderParentRepository orderParentRepository;
    @GetMapping({"", "/"})
    public String home(){
        return "index";
    }

    // Chuyển hướng người dùng đến cổng thanh toán VNPAY
    @PostMapping("/submitOrder")
    public String submidOrder(@RequestParam("amount") int orderTotal,
                              @RequestParam("orderInfo") String orderInfo,
                              HttpServletRequest request){
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String vnpayUrl = vnPayService.createOrder(request, orderTotal, orderInfo, baseUrl);
        log.info("vnpay url: " + vnpayUrl);
        return "redirect:" + vnpayUrl;
    }

    // Sau khi hoàn tất thanh toán, VNPAY sẽ chuyển hướng trình duyệt về URL này
    @GetMapping("/vnpay-payment-return")
    public String paymentCompleted(HttpServletRequest request, Model model){
        int paymentStatus =vnPayService.orderReturn(request);

        if (paymentStatus == 1) {
            // Thành công, cập nhật trạng thái đơn hàng
            String txnRef = request.getParameter("vnp_TxnRef");
            OrderParent orderParent = orderParentRepository.findById(Long.valueOf(txnRef))
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            orderParent.setStatus("PAID");
            orderParent.setIsPaidBefore(true);
            orderParentRepository.save(orderParent);
        }

        String orderId = request.getParameter("vnp_TxnRef");
        String paymentTime = request.getParameter("vnp_PayDate");
        String transactionId = request.getParameter("vnp_TransactionNo");
        String totalPrice = request.getParameter("vnp_Amount");

        model.addAttribute("orderId", orderId);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("paymentTime", paymentTime);
        model.addAttribute("transactionId", transactionId);

        return paymentStatus == 1 ? "orderSuccess" : "orderFail";
    }
}
