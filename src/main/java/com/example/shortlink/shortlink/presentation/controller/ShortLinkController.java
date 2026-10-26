package com.example.shortlink.shortlink.presentation.controller;

import com.example.shortlink.shared.PageResponse;
import com.example.shortlink.shortlink.application.service.ShortLinkService;
import com.example.shortlink.shortlink.presentation.dto.ShortLinkHistoryResponse;
import com.example.shortlink.shortlink.presentation.dto.ShortLinkRequest;
import com.example.shortlink.shortlink.presentation.dto.ShortLinkResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static com.example.shortlink.shortlink.api.ShoretLinkPath.GET_SHORT_LINK;
import static com.example.shortlink.shortlink.api.ShoretLinkPath.SHORT_LINK_API;

@RestController
@RequestMapping()
public class ShortLinkController {

    private final ShortLinkService shortLinkService;

    public ShortLinkController(ShortLinkService shortLinkService) {
        this.shortLinkService = shortLinkService;
    }

    @PostMapping(SHORT_LINK_API + GET_SHORT_LINK)
    public ResponseEntity<ShortLinkResponse> getShortLink(@RequestBody ShortLinkRequest request) {
        return ResponseEntity.ok(shortLinkService.getShortLink(request.getOriginalUrl()));
    }

    @GetMapping("/s/{code}")
    public void redirectToOriginalUrl(HttpServletResponse response, @PathVariable String code) throws IOException {
        String originalUrl = shortLinkService.getOriginalUrl(code);
        response.sendRedirect(originalUrl);
    }

    @GetMapping(SHORT_LINK_API + "/history")
    public ResponseEntity<PageResponse<ShortLinkHistoryResponse>> getHistory(Pageable pageable) {
        return ResponseEntity.ok(shortLinkService.getHistory(pageable));
    }

}
