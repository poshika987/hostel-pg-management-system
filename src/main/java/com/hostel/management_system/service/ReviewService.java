package com.hostel.management_system.service;

import com.hostel.management_system.model.Review;
import com.hostel.management_system.model.Student;
import com.hostel.management_system.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Transactional
    public Review submitReview(Student student, int rating, String category, String comments) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }

        Review review = new Review();
        review.setStudent(student);
        review.setRating(rating);
        review.setCategory(clean(category));
        review.setComments(clean(comments));
        return reviewRepository.save(review);
    }

    public List<Review> getReviewsFor(Student student) {
        return reviewRepository.findByStudentOrderByCreatedAtDesc(student);
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }
}
