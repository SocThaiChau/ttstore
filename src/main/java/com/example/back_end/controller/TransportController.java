package com.example.back_end.controller;

import com.example.back_end.model.dto.category.CategoryDTO;
import com.example.back_end.model.dto.transport.TransportDTO;
import com.example.back_end.model.entity.Transport;
import com.example.back_end.service.impl.TransportService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transport")
public class TransportController {

    @Autowired
    private TransportService transportService;
    @GetMapping("/getAll")
    public ResponseEntity<?> getAllTransports() {
        List<TransportDTO> transports = transportService.getAllTransports();
        return ResponseEntity.ok(transports);
    }

    // API thêm mới Transport
    @PostMapping("/add")
    public ResponseEntity<Transport> addTransport(@RequestBody Transport transport) {
        Transport createdTransport = transportService.addTransport(transport);
        return ResponseEntity.ok(createdTransport);
    }

    // API cập nhật Transport
    @PutMapping("/update/{id}")
    public ResponseEntity<Transport> updateTransport(@PathVariable Long id, @RequestBody Transport updatedTransport) {
        Transport transport = transportService.updateTransport(id, updatedTransport);
        return ResponseEntity.ok(transport);
    }

}
