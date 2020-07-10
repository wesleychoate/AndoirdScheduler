package com.wac.android.finalscheduler;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.wac.android.finalscheduler.dao.AssessmentDao;
import com.wac.android.finalscheduler.dao.CourseDao;
import com.wac.android.finalscheduler.dao.MentorDao;
import com.wac.android.finalscheduler.dao.TermDao;
import com.wac.android.finalscheduler.entities.Assessment;
import com.wac.android.finalscheduler.entities.Course;
import com.wac.android.finalscheduler.entities.Mentor;
import com.wac.android.finalscheduler.entities.Term;
import com.wac.android.finalscheduler.util.Convertors;

import java.time.LocalDate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = { Term.class, Course.class, Mentor.class, Assessment.class }, version = 7)
@TypeConverters({Convertors.class})
public abstract class ScheduleDatabase extends RoomDatabase {

    private static final String DB_NAME = "scheduleDatabase.db";
    private static final int THREAD_COUNT = 4;

    private static volatile ScheduleDatabase instance;

    static final ExecutorService preLoadExec =
            Executors.newFixedThreadPool(THREAD_COUNT);

    static final ExecutorService writeExec =
            Executors.newFixedThreadPool(THREAD_COUNT);


    static synchronized ScheduleDatabase getInstance(Context context) {
        if (instance == null)
            synchronized (ScheduleDatabase.class) {
                if (instance == null)
                    instance = create(context);
            }
        return instance;
    }

    private static ScheduleDatabase create(final Context context) {
        return Room.databaseBuilder(
                context,
                ScheduleDatabase.class,
                DB_NAME)
                .addCallback(dbCreateCallback)
                .fallbackToDestructiveMigration()
                .build();
    }

    private static Callback dbCreateCallback = new Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

            preLoadExec.execute(() -> {
                //instance.getCourseDao().deleteAllCourses();
                TermDao dao = instance.getTermDao();
                //dao.deleteAllTerms();

                Term testTerm = new Term("Test 1",
                        LocalDate.of(2020, 1, 1),
                        LocalDate.of(2020, 2, 29));
                //dao.insert(testTerm);

                Mentor testMentor = new Mentor("Test Mentor 1", "123-987-6054", "testmentoremail@email.com");
                MentorDao mDao = instance.getMentorDao();
                //mDao.deleteAllMentors();
                //mDao.insert(testMentor);

                Mentor testMentor2 = new Mentor("Test Mentor 2", "123-987-6054", "testmentoremail@email.com");
                //mDao.insert(testMentor2);

                Course testCourse = new Course("Test Course",
                        LocalDate.of(2020, 1, 1),
                        LocalDate.of(2020, 2, 29),
                        "Started", 1, false, false, 1, "Note", null, null);
                //instance.getCourseDao().insert(testCourse);
            });
        }
    };

    public abstract TermDao getTermDao();
    public abstract CourseDao getCourseDao();
    public abstract MentorDao getMentorDao();
    public abstract AssessmentDao getAssessmentDao();
}
