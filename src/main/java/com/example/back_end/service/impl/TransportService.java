package com.example.back_end.service.impl;

import com.example.back_end.model.dto.category.CategoryDTO;
import com.example.back_end.model.dto.transport.TransportDTO;
import com.example.back_end.model.entity.Category;
import com.example.back_end.model.entity.Transport;
import com.example.back_end.repository.CategoryRepository;
import com.example.back_end.repository.TransportRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
@Slf4j
public class TransportService {

    ModelMapper modelMapper;
    @Autowired
    private TransportRepository transportRepository;


    public List<TransportDTO> getAllTransports() {
        try {
            List<Transport> transports = transportRepository.findAll();
            return transports.stream()
                    .map(transport -> modelMapper.map(transport, TransportDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            throw ex;
        }
    }

    // Thêm mới Transport
    public Transport addTransport(Transport transport) {
        return transportRepository.save(transport);
    }

    // Cập nhật Transport
    public Transport updateTransport(Long id, Transport updatedTransport) {
        Optional<Transport> existingTransport = transportRepository.findById(id);
        if (existingTransport.isPresent()) {
            Transport transport = existingTransport.get();
            transport.setName(updatedTransport.getName());
            // Cập nhật các thuộc tính khác nếu cần
            return transportRepository.save(transport);
        } else {
            throw new RuntimeException("Transport not found with id: " + id);
        }
    }

}
