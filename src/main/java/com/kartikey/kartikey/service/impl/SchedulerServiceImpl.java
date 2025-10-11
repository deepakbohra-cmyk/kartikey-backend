package com.kartikey.kartikey.service.impl;

import com.kartikey.kartikey.entity.*;
import com.kartikey.kartikey.repository.FeedBackRepository;
import com.kartikey.kartikey.repository.FormDataRepository;
import com.kartikey.kartikey.repository.QcFormDataRepository;
import com.kartikey.kartikey.repository.UserMetricsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SchedulerServiceImpl {

    private final FeedBackRepository feedBackRepository;
    private final FormDataRepository formDataRepository;
    private final UserMetricsRepository userMetricsRepository;
    private final QcFormDataRepository qcFormDataRepository;

    // 🕓 4 AM → Delete old FormData if no open feedback or linked QC
    @Scheduled(cron = "0 0 4 * * ?")
    public void deleteOldFormData() {
        LocalDateTime twoWeeksAgo = LocalDateTime.now().minusWeeks(2);

        List<FormData> oldForms = formDataRepository.findByCreatedAtBefore(twoWeeksAgo);

        for (FormData form : oldForms) {
            boolean hasOpenFeedback = feedBackRepository.existsByFormDataAndStatusNot(form, FeedBack.Status.CLOSED);
            boolean hasQc = qcFormDataRepository.existsByFormData(form);

            if (!hasOpenFeedback && !hasQc) {
                try {
                    formDataRepository.delete(form);
                    System.out.println("🗑 Deleted FormData ID: " + form.getId());
                } catch (Exception e) {
                    System.err.println("❌ Could not delete FormData ID " + form.getId() + ": " + e.getMessage());
                }
            }
        }
    }

    // 🕒 3 AM → Delete old QC FormData
    @Scheduled(cron = "0 0 3 * * ?")
    public void deleteOldQcFormData() {
        LocalDateTime twoWeeksAgo = LocalDateTime.now().minusWeeks(2);
        List<QcFormData> oldForms = qcFormDataRepository.findByCreatedAtBefore(twoWeeksAgo);

        for (QcFormData form : oldForms) {
            try {
                qcFormDataRepository.delete(form);
                System.out.println("🗑 Deleted QCFormData ID: " + form.getId());
            } catch (Exception e) {
                System.err.println("❌ Could not delete QCFormData ID " + form.getId() + ": " + e.getMessage());
            }
        }
    }

    // 🕑 2 AM → Delete closed feedbacks older than 1 week
    @Scheduled(cron = "0 0 2 * * ?")
    public void deleteOldClosedFeedback() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        List<FeedBack> oldClosedFeedbacks =
                feedBackRepository.findByStatusAndUpdatedAtBefore(FeedBack.Status.CLOSED, oneWeekAgo);

        if (!oldClosedFeedbacks.isEmpty()) {
            feedBackRepository.deleteAll(oldClosedFeedbacks);
            System.out.println("🗑 Deleted " + oldClosedFeedbacks.size() + " old CLOSED feedback(s).");
        }
    }

    // 🕛 Every 2 hours → Recalculate feedback scores
    @Scheduled(cron = "0 0 0/2 * * ?")
    public void setFeedBackScore() {
        List<UserMetrics> allMetrics = userMetricsRepository.findAll();

        for (UserMetrics metrics : allMetrics) {
            UserEntity user = metrics.getUser();

            if (user.getRole() == UserEntity.Role.L1TEAM) {
                Long feedbackGiven = metrics.getFeedbackGiven() != null ? metrics.getFeedbackGiven() : 0L;
                Long qcFilled = metrics.getFormChecked() != null ? metrics.getFormChecked() : 0L;

                double score = 0;
                if (feedbackGiven > 0) {
                    score = ((double) qcFilled / feedbackGiven) * 100;
                }

                metrics.setScore(score);
                metrics.setUpdatedAt(LocalDateTime.now());
            }
        }

        userMetricsRepository.saveAll(allMetrics);
        System.out.println("✅ User metrics scores updated for L1TEAM users.");
    }
}
