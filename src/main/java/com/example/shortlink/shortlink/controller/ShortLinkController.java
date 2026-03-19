package com.example.shortlink.shortlink.controller;

import com.example.shortlink.shortlink.domain.ShortLinkService;
import com.example.shortlink.shortlink.domain.dto.ShortLinkRequest;
import com.example.shortlink.shortlink.domain.dto.ShortLinkResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static com.example.shortlink.shared.Constant.SHORT_LINK_URL;
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
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String code) {
        String originalUrl = shortLinkService.getOriginalUrl(code);
        return ResponseEntity.status(302).location(URI.create(originalUrl)).build();
    }

}
