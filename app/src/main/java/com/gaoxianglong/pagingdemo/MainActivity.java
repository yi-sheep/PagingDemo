package com.gaoxianglong.pagingdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    Button mButtonPopulate, mButtonClear;
    StudentDao mStudentDao;
    StudentDatabase mStudentDatabase;
    MyPagedAdapter mPagedAdapter;
    LiveData<PagedList<Student>> allStudentLivePaged;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)); // 设置布局为LinearLayoutManager
        mPagedAdapter = new MyPagedAdapter(); // 初始化适配器
        mRecyclerView.setAdapter(mPagedAdapter); // 设置适配器
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL)); // 设置分割线

        mStudentDatabase = StudentDatabase.getInstance(this); // 初始化database
        mStudentDao = mStudentDatabase.getStudentDao(); // 初始化Dao

        // 初始化分页列表
        // mStudentDao.getAllStudents() 数据来源
        // pageSize 要加载的页面大小，越小越能看出在做分也加载，快速滚动列表的时候会出现itemView有了，但是数据还没有出来
        allStudentLivePaged = new LivePagedListBuilder<>(mStudentDao.getAllStudents(), 2)
                .build();
        // 开启liveData的数据监听
        allStudentLivePaged.observe(this,students -> {
            mPagedAdapter.submitList(students); // 将改变后的数据列表提交到适配器进行比较
            // 让观察分页更明显
            students.addWeakCallback(null, new PagedList.Callback() {
                @Override
                public void onChanged(int position, int count) {
                    Log.d("myLog", ""+students);
                }

                @Override
                public void onInserted(int position, int count) {

                }

                @Override
                public void onRemoved(int position, int count) {

                }
            });
        });

        mButtonPopulate = findViewById(R.id.buttonPopulate);
        mButtonPopulate.setOnClickListener(v -> {
            Student[] students = new Student[100]; // 定义一个Student数组长度为100
            // 遍历100次，每一次生成一个Student对象，插入数据，然后再设置给数组
            for (int i = 0; i < 100; i++) {
                Student student = new Student();
                student.setStudentNumber(i);
                students[i] = student;
            }
            new InsertAsyncTask(mStudentDao).execute(students); // 操作数据库插入数据，传入数组
        });
        mButtonClear = findViewById(R.id.buttonClear);
        mButtonClear.setOnClickListener(v -> {
            new DeleteAllAsyncTask(mStudentDao).execute(); // 操作数据库删除所有数据
        });

    }

    // 使用异步操作数据库
    static class InsertAsyncTask extends AsyncTask<Student, Void, Void> {
        // 因为是静态内部类，不能直接访问外部类的对象，所以需要一个构造方法来让外部类在使用的时候传入，要用到的对象
        StudentDao mStudentDao;

        public InsertAsyncTask(StudentDao studentDao) {
            mStudentDao = studentDao;
        }

        @Override
        protected Void doInBackground(Student... students) {
            mStudentDao.insertStudents(students);
            return null;
        }
    }

    static class DeleteAllAsyncTask extends AsyncTask<Void, Void, Void> {
        StudentDao mStudentDao;

        public DeleteAllAsyncTask(StudentDao studentDao) {
            mStudentDao = studentDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mStudentDao.deleteAllStudent();
            return null;
        }
    }
}
