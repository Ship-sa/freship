package me.yeon.freship.product.service;

import me.yeon.freship.common.utils.RedisUtils;
import me.yeon.freship.product.infrastructure.ProductRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private RedisUtils redisUtils;

    @InjectMocks
    private ProductService productService;



}