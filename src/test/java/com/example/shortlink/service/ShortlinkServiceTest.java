package com.example.shortlink.service;

import com.example.shortlink.shared.utils.UserUtils;
import com.example.shortlink.shortlink.application.service.ShortLinkService;
import com.example.shortlink.shortlink.domain.repository.ShortlinkEntityRepository;
import com.example.shortlink.shortlink.presentation.dto.ShortLinkResponse;
import com.example.shortlink.shortlink.presentation.mapper.ShortLinkMapper;
import com.example.shortlink.user.data.repository.UserRepository;
import com.example.shortlink.user.domain.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ShortlinkServiceTest {
    @Mock
    private ShortlinkEntityRepository shortlinkEntityRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ShortLinkMapper shortLinkMapper;

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RMap<String, String> rMap;

    @InjectMocks
    private ShortLinkService shortLinkService;

    @Test
    @SuppressWarnings("unchecked")
    void getShortLink_shouldCreateNew_whenNotExist() {
        String url = "https://google.com";
        String testEmail = "user@example.com";
        User user = new User();

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(shortlinkEntityRepository.findByOriginalUrl(url)).thenReturn(Optional.empty());

        // mock redis
        when(redissonClient.<String, String>getMap(anyString())).thenReturn(rMap);

        try (MockedStatic<UserUtils> mockedUserUtils = mockStatic(UserUtils.class)) {

            mockedUserUtils.when(UserUtils::extractEmailFromCookie).thenReturn(testEmail);

            // Execute the service call INSIDE the try block while the mock is active
            ShortLinkResponse response = shortLinkService.getShortLink(url);

            assertTrue(response.isSuccess());
            assertNotNull(response.getData());

            verify(shortlinkEntityRepository).save(any());
            verify(rMap).put(any(), eq(url));
        }
    }

    @Test
    void search() {
        int[] nums = {-1, 0, 2, 4, 6, 8};
        int target = 4;
        int left = 0;
        int right = nums.length - 1;
        if (nums.length < 2) {
            System.out.println(nums[0] == target ? 0 : -1);
        }
        System.out.println(binarySearch(nums, target, left, right));
    }

    private int binarySearch(int[] nums, int target, int left, int right) {
        int mid = (right + left + 1) / 2;
        if (left >= right)
            return -1;
        if (nums[mid] == target) return mid;
        else if (nums[mid] < target) {
            left = mid + 1;
            return binarySearch(nums, target, left, right);
        } else if (nums[mid] > target) {
            right = mid - 1;
            return binarySearch(nums, target, left, right);
        }
        return -1;
    }
}
