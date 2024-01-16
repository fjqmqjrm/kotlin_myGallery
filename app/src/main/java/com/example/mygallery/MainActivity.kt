package com.example.mygallery

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2

class MainActivity : AppCompatActivity() {

    private val REQUEST_READ_EXTERNAL_STORAGE = 1000
    lateinit var viewPager : ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager = findViewById(R.id.viewPager)
        // 권한이 부여되었는지 확인
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            // 권한이 허용되지 않음
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                // 이전에 거부한 적이 있으면(경고)
                val dlg = AlertDialog.Builder(this)
                dlg.setTitle("권한이 필요한 이유")
                dlg.setMessage("사진 정보를 얻기 위해서는 외부 저장소 권한이 필수로 필요합니다.")
                // 권한 허용 승인 버튼
                dlg.setPositiveButton("확인") { dialog, which ->
                    ActivityCompat.requestPermissions(
                        this@MainActivity,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        REQUEST_READ_EXTERNAL_STORAGE
                    )
                }
                // 권한 허용 거부 버튼
                dlg.setNegativeButton("취소", null)
                dlg.show() // 빠뜨린 부분
            } else {
                // 처음 권한 요청
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_READ_EXTERNAL_STORAGE
                )
            }
        } else {
            // 권한이 이미 제대로 허용됨
            getAllPhotos()
        }
    }

    // 모든 사진 정보 가져오기
    private fun getAllPhotos() {
        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null, // 가져올 항목 배열
            null, // 조건
            null, // 조건
            MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC"
        ) // 촬영 최신날짜순
        val fragments = ArrayList<Fragment>()

        if (cursor != null) {
            // 커서 객체의 다음 경로를 가져옴
            while (cursor.moveToNext()) {
                // 사진 경로 uri 가져오기
                val uri =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                Log.d("MainActivity", uri)
                fragments.add(PhotoFragment.newInstance(uri))
            }
            cursor.close()
        }
        val adapter = MyPagerAdapter(supportFragmentManager)
        adapter.updateFragments(fragments)
        viewPager.adapter = adapter
    }
}
