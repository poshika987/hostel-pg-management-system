package com.hostel.management_system.service.report;

import com.hostel.management_system.model.Review;
import com.hostel.management_system.repository.ReviewRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class ReviewReport extends AbstractReport<Review> {

    private final ReviewRepository reviewRepository;

    public ReviewReport(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public boolean supports(String reportType) {
        return "review".equals(reportType);
    }

    @Override
    protected String title() {
        return "Review Report";
    }

    @Override
    protected List<String> headers() {
        return List.of("Review ID", "Student", "Rating", "Category", "Comments", "Created At");
    }

    @Override
    protected List<Review> retrieveData(LocalDate startDate, LocalDate endDate) {
        return reviewRepository.findAll().stream()
                .filter(review -> inRange(review.getCreatedAt(), startDate, endDate))
                .sorted(Comparator.comparing(Review::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    @Override
    protected List<List<String>> rows(List<Review> records) {
        return records.stream()
                .map(review -> List.of(
                        String.valueOf(review.getReviewId()),
                        review.getStudent() == null ? "Anonymous" : review.getStudent().getName(),
                        String.valueOf(review.getRating()),
                        nullSafe(review.getCategory()),
                        nullSafe(review.getComments()),
                        format(review.getCreatedAt())
                ))
                .toList();
    }

    @Override
    protected Map<String, String> summary(List<Review> records) {
        double average = records.stream().mapToInt(Review::getRating).average().orElse(0);
        return orderedSummary(
                "Records", String.valueOf(records.size()),
                "Average Rating", String.format(Locale.US, "%.1f / 5", average)
        );
    }
}
