package com.example.logistic_service.repository;

import com.example.logistic_service.entity.DeliveryTrack;

import java.util.List;

public interface DeliveryTrackRepository {
    List<DeliveryTrack> findByTracksAt(String tracksAt);

    List<DeliveryTrack> findLocationTracks(String location);
}