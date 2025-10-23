package org.pms.shipments.model;

import java.time.LocalDateTime;

public class TrackingDetail {
    private Integer id;
    private String arithmosApostolis;
    private LocalDateTime checkpointDateTime;
    private String checkpointAction;
    private String checkpointLocation;
    private String checkpointNotes;
    private LocalDateTime createdAt;

    public TrackingDetail() {}

    public TrackingDetail(String arithmosApostolis, LocalDateTime checkpointDateTime,
                          String checkpointAction, String checkpointLocation, String checkpointNotes) {
        this.arithmosApostolis = arithmosApostolis;
        this.checkpointDateTime = checkpointDateTime;
        this.checkpointAction = checkpointAction;
        this.checkpointLocation = checkpointLocation;
        this.checkpointNotes = checkpointNotes;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getArithmosApostolis() { return arithmosApostolis; }
    public void setArithmosApostolis(String arithmosApostolis) { this.arithmosApostolis = arithmosApostolis; }

    public LocalDateTime getCheckpointDateTime() { return checkpointDateTime; }
    public void setCheckpointDateTime(LocalDateTime checkpointDateTime) { this.checkpointDateTime = checkpointDateTime; }

    public String getCheckpointAction() { return checkpointAction; }
    public void setCheckpointAction(String checkpointAction) { this.checkpointAction = checkpointAction; }

    public String getCheckpointLocation() { return checkpointLocation; }
    public void setCheckpointLocation(String checkpointLocation) { this.checkpointLocation = checkpointLocation; }

    public String getCheckpointNotes() { return checkpointNotes; }
    public void setCheckpointNotes(String checkpointNotes) { this.checkpointNotes = checkpointNotes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}