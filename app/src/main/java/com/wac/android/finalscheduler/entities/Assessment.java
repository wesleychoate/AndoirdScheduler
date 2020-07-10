package com.wac.android.finalscheduler.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.util.UUID;

@Entity(tableName = "assessments",
        foreignKeys = @ForeignKey(entity = Course.class, parentColumns = "id", childColumns = "courseId",  onDelete = ForeignKey.CASCADE),
        indices = {@Index("courseId")})
public class Assessment implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private Integer id;

    private String title;
    private LocalDate dueDate;
    private Boolean dueDateAlert;
    private UUID jobId;
    private String type;

    private Integer courseId;

    public Assessment(String title, Integer courseId, LocalDate dueDate, Boolean dueDateAlert, String type, UUID jobId) {
        this.title = title;
        this.courseId = courseId;
        this.dueDate = dueDate;
        this.dueDateAlert = dueDateAlert;
        this.type = type;
        this.jobId = jobId;
    }

    protected Assessment(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        title = in.readString();
        byte tmpDueDateAlert = in.readByte();
        dueDateAlert = tmpDueDateAlert == 0 ? null : tmpDueDateAlert == 1;
        type = in.readString();
        if (in.readByte() == 0) {
            courseId = null;
        } else {
            courseId = in.readInt();
        }
        dueDate = (LocalDate) in.readSerializable();
        jobId = (UUID) in.readSerializable();
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
        dest.writeByte((byte) (dueDateAlert == null ? 0 : dueDateAlert ? 1 : 2));
        dest.writeString(type);
        if (courseId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(courseId);
        }
        dest.writeSerializable(dueDate);
        dest.writeSerializable(jobId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Assessment> CREATOR = new Creator<Assessment>() {
        @Override
        public Assessment createFromParcel(Parcel in) {
            return new Assessment(in);
        }

        @Override
        public Assessment[] newArray(int size) {
            return new Assessment[size];
        }
    };

    public void setId(Integer id) { this.id = id; }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public LocalDate getDueDate() { return dueDate; }

    public Boolean getDueDateAlert() { return dueDateAlert; }

    public String getType() { return type; }

    public UUID getJobId() { return jobId; }
}
