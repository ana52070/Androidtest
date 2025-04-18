// MainActivity.java
package com.example.my_tcp_car;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.gesture.Gesture;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private UDPClient udpClient = new UDPClient();

    private ImageView room1_control;
    private ImageView room2_control;
    private ImageView room3_control;
    private ImageView mode_chose;
    private ImageView set_clock;

    private  TextView people_detect_text;

    private int room1_color_state = 1;
    private int room2_color_state = 1;
    private int room3_color_state = 1;

    public int bed_color = 1,bed_green = 1,drawing_color = 1,drawing_green = 1,kichen_color = 1,kichen_green = 1,mode = 1;

    //color:白色:1    黄色:2    暖白色:3

    //grenn:亮度:0-5  ->  0-2000-4000-6000-8000-10000

    // 修改为可变的定时器间隔
    private int timerInterval = 20 * 1000; // 默认20秒
    private int remainingSeconds = timerInterval / 1000;
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            updateTimerDisplay();
            timerHandler.postDelayed(this, 1000);
        }
    };


    // 新增定时器显示TextView
    private TextView timerTextView;
    private TextView send;




    public int conncet_flag = 0;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonConnect = findViewById(R.id.buttonConnect);

        room1_control = findViewById(R.id.room1_control);
        room2_control = findViewById(R.id.room2_control);
        room3_control = findViewById(R.id.room3_control);

        people_detect_text = findViewById(R.id.people_detect_text);
        SeekBar seekBar1 = findViewById(R.id.seekBar1);
        SeekBar seekBar2 = findViewById(R.id.seekBar2);
        SeekBar seekBar3 = findViewById(R.id.seekBar3);

        mode_chose = findViewById(R.id.mode_chose);

        set_clock = findViewById(R.id.set_clock);

        TextView light_detect_text = findViewById(R.id.light_detect_text);

        send = findViewById(R.id.send);



        // 初始化定时器显示TextView
        timerTextView = findViewById(R.id.timerTextView);
        startTimer();

        // 设置定时器点击监听
        set_clock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSetTimerDialog();
            }
        });

        udpClient.setDataReceivedListener((data, ip) ->
                runOnUiThread(() -> {

                    String intput = data;

                    //send.setText(data);

                    // 去除字符串的前后方括号
                    intput = intput.substring(1, intput.length() - 1);
                    // 按逗号分割字符串
                    String[] strArray = intput.split(",");
                    int intArray[] = new int[strArray.length];
                    for (int i = 0; i < strArray.length; i++) {
                        try {
                            intArray[i] = Integer.valueOf(strArray[i]);
                        } catch (NumberFormatException e) {
                            System.out.println("字符串 " + strArray[i] + " 无法转换为整数。");
                        }
                    }



                    //更新数据

                    if(intArray[1] == 1)
                    {
                        room3_control.setImageResource(R.drawable.white);
                        room3_color_state = 1;
                    }
                    else if(intArray[1] == 2)
                    {
                        room3_control.setImageResource(R.drawable.yellow);
                        room3_color_state = 2;
                    }
                    else if(intArray[1] == 3)
                    {
                        room3_control.setImageResource(R.drawable.pink);
                        room3_color_state = 3;
                    }



                    if(intArray[3] == 1)
                    {
                        room1_control.setImageResource(R.drawable.white);
                        room1_color_state = 1;
                    }
                    else if(intArray[3] == 2)
                    {
                        room1_control.setImageResource(R.drawable.yellow);
                        room1_color_state = 2;
                    }
                    else if(intArray[3] == 3)
                    {
                        room1_control.setImageResource(R.drawable.pink);
                        room1_color_state = 3;
                    }



                    if(intArray[5] == 1)
                    {
                        room2_control.setImageResource(R.drawable.white);
                        room2_color_state = 1;
                    }
                    else if(intArray[5] == 2)
                    {
                        room2_control.setImageResource(R.drawable.yellow);
                        room2_color_state = 2;
                    }
                    else if(intArray[5] == 3)
                    {
                        room2_control.setImageResource(R.drawable.pink);
                        room2_color_state = 3;
                    }

                    setSeekBarProgress(seekBar3, intArray[2] / 2000);
                    setSeekBarProgress(seekBar1, intArray[4] / 2000);
                    setSeekBarProgress(seekBar2, intArray[6] / 2000);



                    if(intArray[9] == 1)
                    {
                        mode_chose.setImageResource(R.drawable.close);
                        mode = 1;
                    }
                    else if(intArray[9] == 2)
                    {
                        mode_chose.setImageResource(R.drawable.open);
                        mode = 2;
                    }



                    //获取光敏和人体
                    if(intArray[7] == 1)
                    {
                        people_detect_text.setText("状态:有人");
                    }
                    else if(intArray[7] == 0)
                    {
                        people_detect_text.setText("状态:无人");
                    }


                    light_detect_text.setText("强度:" + intArray[8] + "%");



            })
        );

        // 为按钮设置点击监听器
        room1_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 处理按钮点击事件
                room1_control_handleButtonClick();
            }
        });
        room1_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 处理按钮点击事件
                room1_control_handleButtonClick();
            }
        });

        room2_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 处理按钮点击事件
                room2_control_handleButtonClick();
            }
        });
        room2_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 处理按钮点击事件
                room2_control_handleButtonClick();
            }
        });

        room3_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 处理按钮点击事件
                room3_control_handleButtonClick();
            }
        });
        room3_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 处理按钮点击事件
                room3_control_handleButtonClick();
            }
        });
        mode_chose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 处理按钮点击事件
                mode_chose_handleButtonClick();
            }
        });

        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                showProgressDialog1(progress);
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                showProgressDialog2(progress);
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekBar3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                showProgressDialog3(progress);
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });


        buttonConnect.setOnClickListener(v -> {
            // 弹出密码输入对话框
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("请输入连接密码");

            // 设置输入框
            final EditText input = new EditText(MainActivity.this);
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            builder.setView(input);

            // 设置确认按钮
            builder.setPositiveButton("确定", (dialog, which) -> {
                String password = input.getText().toString().trim();
                if (password.equals("123456")) { // 验证密码
                    if (udpClient.isConnected()) {
                        runOnUiThread(() ->
                                Toast.makeText(MainActivity.this, "UDP已初始化", Toast.LENGTH_SHORT).show());
                        return;
                    }

                    new Thread(() -> {
                        boolean initialized = udpClient.initialize("255.255.255.255", 5200);
                        runOnUiThread(() -> {
                            if (initialized) {
                                Toast.makeText(MainActivity.this, "UDP初始化成功", Toast.LENGTH_SHORT).show();
                                conncet_flag = 1;
                            } else {
                                Toast.makeText(MainActivity.this, "初始化失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }).start();
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(MainActivity.this, "密码错误，连接失败", Toast.LENGTH_SHORT).show());
                }
            });



            // 设置取消按钮
            builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());

            // 显示对话框
            builder.create().show();
        });


    }



    // 修改后的startTimer方法
    private void startTimer() {
        timerHandler.removeCallbacks(timerRunnable); // 停止之前的定时器
        remainingSeconds = timerInterval / 1000;
        updateTimerDisplay(); // 立即更新显示
        timerHandler.postDelayed(timerRunnable, 1000);
    }

    // 修改后的updateTimerDisplay方法
    private void updateTimerDisplay() {
        if(conncet_flag == 1) {
            remainingSeconds--;
            timerTextView.setText("剩余时间：" + remainingSeconds + "秒");

            if (remainingSeconds <= 0) {
                showTimerToast();
                remainingSeconds = timerInterval / 1000; // 使用新设置的时间重置
            }
        }
    }

    private void showTimerToast() {
        runOnUiThread(this::send_close

        );
    }

    // 新增设置定时器对话框
    private void showSetTimerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设置倒计时时间（秒）");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String inputStr = input.getText().toString();
                if (!inputStr.isEmpty()) {
                    try {
                        int seconds = Integer.parseInt(inputStr);
                        if (seconds > 0) {
                            timerInterval = seconds * 1000;
                            startTimer(); // 重启定时器
                            Toast.makeText(MainActivity.this, "已设置为" + seconds + "秒", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "请输入正整数", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(MainActivity.this, "输入无效", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void setSeekBarProgress(SeekBar seekBar, int progress) {
        seekBar.setProgress(progress);
    }

    private View.OnTouchListener createHoldListener(String command) {
        return new View.OnTouchListener() {
            private Handler mHandler;
            private final int INITIAL_DELAY = 200;
            private final int NORMAL_DELAY = 100;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mHandler == null) mHandler = new Handler();
                        sendControlCommand(command);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                sendControlCommand(command);
                                mHandler.postDelayed(this, NORMAL_DELAY);
                            }
                        }, INITIAL_DELAY);
                        return true;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        if (mHandler != null) {
                            mHandler.removeCallbacksAndMessages(null);
                            mHandler = null;
                        }
                        return true;
                }
                return false;
            }
        };
    }

    private void showProgressDialog1(int progress) {

        drawing_green = progress * 2000;
        int send_package[] = {1,bed_color,bed_green,drawing_color,drawing_green,kichen_color,kichen_green,mode};
        sendControlCommand(Arrays.toString(send_package) + "\r\n");

    }

    private void showProgressDialog2(int progress) {
        kichen_green = progress * 2000;
        int send_package[] = {1,bed_color,bed_green,drawing_color,drawing_green,kichen_color,kichen_green,mode};
        sendControlCommand(Arrays.toString(send_package) + "\r\n");
    }

    private void showProgressDialog3(int progress) {

        bed_green = progress * 2000;
        int send_package[] = {1,bed_color,bed_green,drawing_color,drawing_green,kichen_color,kichen_green,mode};
        sendControlCommand(Arrays.toString(send_package) + "\r\n");
    }

    private void sendControlCommand(String command) {
        new Thread(() -> {
            udpClient.sendData(command);
//            runOnUiThread(() ->
//                    Toast.makeText(this, "发送: " + command, Toast.LENGTH_SHORT).show()
//            );
        }).start();
    }

    private void room1_control_handleButtonClick() {
        // 在这里编写按钮点击后的逻辑
        //黄色
        if (room1_color_state == 1) {
            room1_control.setImageResource(R.drawable.yellow);
            drawing_color = 2;
            room1_color_state++;
            int send_package[] = {1,bed_color,bed_green,drawing_color,drawing_green,kichen_color,kichen_green,mode};
            sendControlCommand(Arrays.toString(send_package) + "\r\n");
        } else if (room1_color_state == 2) {
            room1_control.setImageResource(R.drawable.pink);
            room1_color_state++;
            drawing_color = 3;
            int send_package[] = {1,bed_color,bed_green,drawing_color,drawing_green,kichen_color,kichen_green,mode};
            sendControlCommand(Arrays.toString(send_package) + "\r\n");
        }
        else if (room1_color_state == 3) {
            room1_control.setImageResource(R.drawable.white);
            room1_color_state = 1;
            drawing_color = 1;
            int send_package[] = {1,bed_color,bed_green,drawing_color,drawing_green,kichen_color,kichen_green,mode};
            sendControlCommand(Arrays.toString(send_package) + "\r\n");
        }
    }

    private void room2_control_handleButtonClick() {
        // 在这里编写按钮点击后的逻辑
        //黄色
        if (room2_color_state == 1) {
            room2_control.setImageResource(R.drawable.yellow);
            kichen_color = 2;
            room2_color_state++;
            int send_package[] = {1,bed_color,bed_green,drawing_color,drawing_green,kichen_color,kichen_green,mode};
            sendControlCommand(Arrays.toString(send_package) + "\r\n");
        } else if (room2_color_state == 2) {
            room2_control.setImageResource(R.drawable.pink);
            kichen_color = 3;
            room2_color_state++;
            int send_package[] = {1,bed_color,bed_green,drawing_color,drawing_green,kichen_color,kichen_green,mode};
            sendControlCommand(Arrays.toString(send_package) + "\r\n");
        }
        else if (room2_color_state == 3) {
            room2_control.setImageResource(R.drawable.white);
            kichen_color = 1;
            room2_color_state = 1;
            int send_package[] = {1,bed_color,bed_green,drawing_color,drawing_green,kichen_color,kichen_green,mode};
            sendControlCommand(Arrays.toString(send_package) + "\r\n");
        }
    }

    private void room3_control_handleButtonClick() {
        // 在这里编写按钮点击后的逻辑
        //黄色
        if (room3_color_state == 1) {
            room3_control.setImageResource(R.drawable.yellow);
            bed_color = 2;
            room3_color_state++;
            int send_package[] = {1,bed_color,bed_green,drawing_color,drawing_green,kichen_color,kichen_green,mode};
            sendControlCommand(Arrays.toString(send_package) + "\r\n");
        } else if (room3_color_state == 2) {
            room3_control.setImageResource(R.drawable.pink);
            bed_color = 3;
            room3_color_state++;
            int send_package[] = {1,bed_color,bed_green,drawing_color,drawing_green,kichen_color,kichen_green,mode};
            sendControlCommand(Arrays.toString(send_package) + "\r\n");
        }
        else if (room3_color_state == 3) {
            room3_control.setImageResource(R.drawable.white);
            bed_color = 1;
            room3_color_state = 1;
            int send_package[] = {1,bed_color,bed_green,drawing_color,drawing_green,kichen_color,kichen_green,mode};
            sendControlCommand(Arrays.toString(send_package) + "\r\n");
        }
    }

    private void mode_chose_handleButtonClick() {
        // 在这里编写按钮点击后的逻辑
        //黄色
        if (mode == 1) {
            mode_chose.setImageResource(R.drawable.open);

            mode = 2;
            int send_package[] = {1,bed_color,bed_green,drawing_color,drawing_green,kichen_color,kichen_green,mode};
            sendControlCommand(Arrays.toString(send_package) + "\r\n");
        } else if (mode == 2) {
            mode_chose.setImageResource(R.drawable.close);
            mode = 1;
            int send_package[] = {1,bed_color,bed_green,drawing_color,drawing_green,kichen_color,kichen_green,mode};
            sendControlCommand(Arrays.toString(send_package) + "\r\n");
        }

    }

    private void send_close() {
        bed_green = 0;
        drawing_green = 0;
        kichen_green = 0;
        sendControlCommand(Arrays.toString(new int[]{1,bed_color,0,drawing_color,0,kichen_color,0,mode}) + "\r\n");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        udpClient.close();
        udpClient.setDataReceivedListener(null);
    }
}