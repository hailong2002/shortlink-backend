package com.example.shortlink.shortlink.controller;

import com.example.shortlink.shortlink.domain.ShortLinkService;
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

    @GetMapping(SHORT_LINK_API + GET_SHORT_LINK)
    public ResponseEntity<String> getShortLink(@RequestParam String originalUrl) {
        String code = shortLinkService.getShortLink(originalUrl);
        return ResponseEntity.ok(SHORT_LINK_URL + code);
    }

    @GetMapping("/short")
    public ResponseEntity<Void> redirectToOriginalUrl(@RequestParam String code) {
        String originalUrl = shortLinkService.getOriginalUrl(code);
        return ResponseEntity.status(302).location(URI.create(originalUrl)).build();
    }

}
