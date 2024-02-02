package com.ssafy.yourpilling.push.controller;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.ssafy.yourpilling.push.controller.dto.request.*;
import com.ssafy.yourpilling.push.controller.mapper.PushControllerMapper;
import com.ssafy.yourpilling.push.model.dao.entity.DeviceToken;
import com.ssafy.yourpilling.push.model.dao.entity.PushNotification;
import com.ssafy.yourpilling.push.model.service.PushService;
import com.ssafy.yourpilling.push.model.service.vo.out.OutNotificationsVo;
import com.ssafy.yourpilling.push.model.service.vo.out.OutPushMessageInfoMapVo;
import com.ssafy.yourpilling.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/push")
public class PushController {

    private final PushService pushService;
    private final PushControllerMapper mapper;
    private final FirebaseMessaging firebaseMessaging;

    @PostMapping("/device-token")
    ResponseEntity<Void> register(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                  @RequestBody RequestDeviceTokenDto dto) {
        pushService.register(mapper.mapToDeviceTokenVo(principalDetails.getMember().getMemberId(), dto));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/notification")
    ResponseEntity<OutPushMessageInfoMapVo> selectPushNotifications(@AuthenticationPrincipal PrincipalDetails principalDetails) {

        OutPushMessageInfoMapVo vo = pushService.selectPushNotification(principalDetails.getMember().getMemberId());
        return ResponseEntity.ok(vo);
    }

    @PostMapping("/notification")
    ResponseEntity<Void> registPushNotification(@RequestBody RequestPushNotificationsDto dto) {

        pushService.registPushNotification(mapper.mapToRegistPushNotificationVo(dto));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/notification")
    ResponseEntity<Void> updatePushNotification(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                @RequestBody RequestUpdatePushNotificationDto dto) {

        pushService.updatePushNotification(mapper.mapToUpdatePushNotificationVo(principalDetails.getMember().getMemberId(), dto));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/notification")
    ResponseEntity<Void> deletePushNotification(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                @RequestBody RequestDeletePushNotificationsDto dto) {

        pushService.DeletePushNotification(mapper.mapToDeletePushNotificationVo(principalDetails.getMember().getMemberId(), dto));
        return ResponseEntity.ok().build();
    }

    @Scheduled(cron = "0 */1 * * * *")
    ResponseEntity<Void> sendPushMessage() {

        LocalDateTime now = LocalDateTime.now();

        RequestPushFcmDto dto = RequestPushFcmDto
                .builder()
                .pushDay(now.getDayOfWeek().getValue())
                .hour(now.getHour())
                .minute(now.getMinute())
                .build();

        OutNotificationsVo vo = pushService.findAllByPushDayAndPushTime(mapper.mapToPushNotificationsVo(dto));

        for (PushNotification noti : vo.getPushNotifications()) {
            for (DeviceToken deviceToken : noti.getPushOwnPill().getMember().getDeviceTokens()) {
                if (deviceToken.getDeviceToken() == null) {
                    System.err.println("디바이스 토큰이 존재하지 않습니다!");
                    continue;
                }

                Message fcmMessage = Message
                        .builder()
                        .setNotification(getNotification(noti))
                        .setToken(deviceToken.getDeviceToken())
                        .build();

                try {
                    firebaseMessaging.send(fcmMessage);
                } catch (FirebaseMessagingException e) {
                    e.printStackTrace();
                }
            }
        }

        return ResponseEntity.ok().build();
    }


    private static Notification getNotification(PushNotification noti) {
        return Notification.builder().setBody(noti.getMessage()).build();
    }

    @GetMapping("/send-pushMessageTest")
    ResponseEntity<Void> sendPushMessageTest() {

        Message fcmMessage = Message
                .builder()
                .setNotification(
                        Notification
                                .builder()
                                .setTitle("Pillin")
                                .setBody("안녕하세요? 여러분의 건강을 책임지는 Pillin 입니다! Pillin은 영양제 재고 관리 및 푸시 알림을 활용하여 여러분의 무병장수를 기원하며 시작되었습니다.")
                                .setImage("https://ifh.cc/g/pWq9AB.png")
                                .build()
                )
                .setToken("cM1S9vPATQad-3ankU0GYr:APA91bF-zg_kuFfbqviT1PN6sqxPnwdwe_cX1wMI3Y1LeIt0TuNf7d082HwrHnW131_m7AcnxNk_Fd_9HqC2y0oCZ1lLTD3gwOeEbGZtbqOlb2uEz1Sg_EtemIZiLh2792ZiJ_jUAOi9")
                .build();
        try {
            firebaseMessaging.send(fcmMessage);
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok().build();
    }


//    @Scheduled(cron = "*/10 * * * * *")
//    void sendPushMessageScheduleTest() {
//        System.out.println("cron");
//        Message fcmMessage = Message
//                .builder()
//                .setNotification(
//                        Notification
//                                .builder()
//                                .setTitle("Pillin")
//                                .setBody("안녕하세요? 여러분의 건강을 책임지는 Pillin 입니다! Pillin은 영양제 재고 관리 및 푸시 알림을 활용하여 여러분의 무병장수를 기원하며 시작되었습니다.")
//                                .setImage("https://www.google.com/url?sa=i&url=https%3A%2F%2Fpixabay.com%2Fko%2Fimages%2Fsearch%2F%25EC%2598%2581%25EC%2596%2591%25EC%25A0%259C%2F&psig=AOvVaw2J4FYwok9I3UwNP5WIPR-_&ust=1706684262130000&source=images&cd=vfe&opi=89978449&ved=0CBIQjRxqFwoTCNiij7vEhIQDFQAAAAAdAAAAABAE")
//                                .build()
//                )
//                .setToken("ecuYPtVzT7a6WpZ_Lh9SOZ:APA91bFWgs3Pc-jN4EYy_9cqPA8379nXeS3hhWcstoPtMN-kHIK43fwmV76TVEhiF4f2GZMqPtU5zD-tRDPHiu-HSujS31G4i7sjbWgmdegv7cEj-_b8xm1LMbxDvhj5ivVkPH20qxe7")
//                .build();
//        try{
//            firebaseMessaging.send(fcmMessage);
//        } catch (FirebaseMessagingException e) {
//            e.printStackTrace();
//        }
//    }
}
