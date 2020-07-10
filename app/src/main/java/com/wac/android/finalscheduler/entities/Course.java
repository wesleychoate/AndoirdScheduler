package com.wac.android.finalscheduler.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.util.UUID;

@Entity(tableName = "courses",
        foreignKeys = {@ForeignKey(entity = Term.class, parentColumns = "id", childColumns = "termId", onDelete = ForeignKey.RESTRICT),
                @ForeignKey(entity = Mentor.class, parentColumns = "id", childColumns = "mentorId", onDelete = ForeignKey.RESTRICT)},
        indices = {@Index("termId")})
public class Course implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private Integer id;

    private String title;
    private LocalDate startDate;
    private Boolean startDateAlert = false;
    private LocalDate endDate;
    private Boolean endDateAlert = false;
    private String status;
    private String note;

    private Integer termId;
    private Integer mentorId;

    public UUID getStartJobId() {
        return startJobId;
    }

    public UUID getEndJobId() {
        return endJobId;
    }

    private UUID startJobId, endJobId;

    public Course(String title, LocalDate startDate, LocalDate endDate, String status,
                  Integer termId, Boolean startDateAlert, Boolean endDateAlert,
                  Integer mentorId, String note, UUID startJobId, UUID endJobId) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.termId = termId;
        this.startDateAlert = startDateAlert;
        this.endDateAlert = endDateAlert;
        this.mentorId = mentorId;
        this.note = note;
        this.startJobId = startJobId;
        this.endJobId = endJobId;
    }

    protected Course(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        title = in.readString();
        startDate = (LocalDate) in.readSerializable();
        endDate = (LocalDate) in.readSerializable();
        byte tmpStartDateAlert = in.readByte();
        startDateAlert = tmpStartDateAlert == 0 ? null : tmpStartDateAlert == 1;
        byte tmpEndDateAlert = in.readByte();
        endDateAlert = tmpEndDateAlert == 0 ? null : tmpEndDateAlert == 1;
        status = in.readString();
        note = in.readString();
        if (in.readByte() == 0) {
            termId = null;
        } else {
            termId = in.readInt();
        }
        if (in.readByte() == 0) {
            mentorId = null;
        } else {
            mentorId = in.readInt();
        }
        startJobId = (UUID) in.readSerializable();
        endJobId = (UUID) in.readSerializable();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeString(title);
        dest.writeSerializable(startDate);
        dest.writeSerializable(endDate);
        dest.writeByte((byte) (startDateAlert == null ? 0 : startDateAlert ? 1 : 2));
        dest.writeByte((byte) (endDateAlert == null ? 0 : endDateAlert ? 1 : 2));
        dest.writeString(status);
        dest.writeString(note);
        if (termId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(termId);
        }
        if (mentorId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(mentorId);
        }
        dest.writeSerializable(startJobId);
        dest.writeSerializable(endJobId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Course> CREATOR = new Creator<Course>() {
        @Override
        public Course createFromParcel(Parcel in) {
            return new Course(in);
        }

        @Override
        public Course[] newArray(int size) {
            return new Course[size];
        }
    };

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getStatus() {
        return status;
    }

    public Integer getTermId() {
        return termId;
    }

    public Integer getMentorId() { return mentorId; }

    public Boolean getStartDateAlert() { return startDateAlert; }

    public Boolean getEndDateAlert() { return endDateAlert; }

    public String getNote() { return note; }
}
