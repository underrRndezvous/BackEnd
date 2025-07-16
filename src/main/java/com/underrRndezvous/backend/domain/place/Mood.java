package com.underrRndezvous.backend.domain.place;

import com.underrRndezvous.backend.domain.meeting.CategoryMood;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "moods")
public class Mood {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mood_id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private PlaceCategory category;

    @OneToMany(mappedBy = "mood", cascade = CascadeType.ALL)
    private List<CategoryMood> categoryMoods;
}