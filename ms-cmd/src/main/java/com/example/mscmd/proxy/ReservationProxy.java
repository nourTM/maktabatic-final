package com.example.mscmd.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "ms-reservation")
public interface ReservationProxy {
    @GetMapping("/api/disponible/{idNotice}")
    Long countDisponible(@PathVariable("idNotice") Long id);
    @GetMapping("/api/verify")
    boolean verifyReservationDisponible( @RequestParam("id") Long idnotice,@RequestParam("rr") String rr);
    @DeleteMapping("/api/delete")
    boolean deleteReservation( @RequestParam("id") Long idnotice,@RequestParam("rr") String rr);
    @GetMapping("/api/waiting/{idNotice}")
    Long countWaiting(@PathVariable("idNotice") Long id);
    @PatchMapping("/api/updatedispo")
    boolean updateDispo(@RequestParam("id") Long id_notice);
}
