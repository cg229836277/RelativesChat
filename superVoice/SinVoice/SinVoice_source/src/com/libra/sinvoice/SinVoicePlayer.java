/*
 * Copyright (C) 2014 gujicheng
 * 未经作者许可，禁止将该程序用于商业用途
 * 
 * 该声波通信程序在前一个开源版本（SinVoice）的基础上，做了许多优化：
 * 优化如下：
 * 1. 识别效率更高，几乎达到100%，完全可以达到商业用途标准，比chirp，支付宝，茄子快传等软件的识别效率更高。
 * 2. 能支持更多复杂场景的识别，在有嘈杂大声的背景音乐，嘈杂的会议室，食堂，公交车，马路，施工场地，
 *     小汽车，KTV等一些复杂的环境下，依然能保持很高的识别率。
 * 3. 能支持更多token的识别，通过编码可以传送所有字符。
 * 4. 通过定制可以实现相同字符的连续传递,比如“234456”。
 * 5. 支持自动纠错功能，在有3个以内字符解码出错的情况下可以自动纠正。
 * 6. 程序运行效率非常高，可以用于智能手机，功能手机，嵌入式设备，PC，平板等嵌入式系统上。
 * 7. 声波的频率声音和音量可定制。
 * 
 * 此demo程序属于试用性质程序，仅具备部分功能，其限制如下：
 * 1. 仅支持部分字符识别。
 * 2. 识别若干次后，程序会自动停止识别。若想继续使用，请停止该程序，然后重新启动程序。
 * 3. 不支持连续字符传递。
 * 4. 不支持自动纠错功能。
 * 5. 禁止用于商业用途。
 * 
 * 若您对完整的声波通信程序感兴趣，请联系作者获取商业授权版本（仅收取苦逼的加班费）。
 *************************************************************************
 **                   作者信息                                                            **
 *************************************************************************
 ** Email: gujicheng197@126.com                                        **
 ** QQ   : 29600731                                                                 **
 ** Weibo: http://weibo.com/gujicheng197                          **
 *************************************************************************
 */
package com.libra.sinvoice;

import android.media.AudioFormat;
import android.media.AudioTrack;

public class SinVoicePlayer implements Encoder.Listener, Encoder.Callback, PcmPlayer.Listener, PcmPlayer.Callback {
    private final static String TAG = "SinVoicePlayer";

    private final static int STATE_START = 1;
    private final static int STATE_STOP = 2;

    private Encoder mEncoder;
    private PcmPlayer mPlayer;
    private BufferQueue mBufferQueue;

    private int mState;
    private Listener mListener;
    private Thread mPlayThread;
    private Thread mEncodeThread;

    private int mSampleRate;
    private int mBufferSize;

    public static final int BITS_8 = 128;
    public static final int BITS_16 = 32768;

    public static interface Listener {
        void onSinVoicePlayStart();

        void onSinVoicePlayEnd();

        void onSinToken(int [] tokens);
    }

    public SinVoicePlayer() {
        this(Common.DEFAULT_SAMPLE_RATE, Common.DEFAULT_BUFFER_COUNT);
    }

    public SinVoicePlayer(int sampleRate, int buffCount) {
        mState = STATE_STOP;

        int bufferSize = AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        LogHelper.d(TAG, "AudioTrackMinBufferSize: " + bufferSize + "  sampleRate:" + sampleRate);

        mBufferQueue = new BufferQueue(buffCount, bufferSize);
        mSampleRate = sampleRate;
        mBufferSize = bufferSize;

        mEncoder = new Encoder(this);
        mEncoder.setListener(this);
        mPlayer = new PcmPlayer(this, sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
        mPlayer.setListener(this);
    }

    public void init() {
        mEncoder.init(mSampleRate, BITS_16, mBufferSize);
    }

    public void uninit() {
        mEncoder.uninit();
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public void play(int tokenLen, final String text) {
        play(tokenLen, false, 0);
    }

    public void play(final int tokenLen, final boolean repeat, final int muteInterval) {
        if (STATE_STOP == mState ) {

            mBufferQueue.set();

            mPlayThread = new Thread() {
                @Override
                public void run() {
                    mPlayer.start();
                }
            };
            if (null != mPlayThread) {
                mPlayThread.start();
            }

            mEncodeThread = new Thread() {
                @Override
                public void run() {
                    mEncoder.start(tokenLen, muteInterval, repeat);
                }
            };
            if (null != mEncodeThread) {
                mEncodeThread.start();
            }

            LogHelper.d(TAG, "play");
            mState = STATE_START;
            if (null != mListener) {
                mListener.onSinVoicePlayStart();
            }
        }
    }

    public synchronized void stop() {
        if (STATE_START == mState) {
            stopEncoder();
            stopPlayer();

            LogHelper.d(TAG, "reset BufferQuque");
            mBufferQueue.reset();

            mState = STATE_STOP;
            LogHelper.d(TAG, "force stop end");
            if (null != mListener) {
                mListener.onSinVoicePlayEnd();
            }
        }
    }

    private void stopEncoder() {
        LogHelper.d(TAG, "start");

        if ( STATE_STOP != mState ) {
            mEncoder.stop();
            LogHelper.d(TAG, "join encode thread");
            synchronized (this) {
                if ( null != mEncodeThread ) {
                    try {
                        LogHelper.d(TAG, "waiting");
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        mEncodeThread = null;
                    }
                }
            }
        }

        LogHelper.d(TAG, "end");
    }

    private void stopPlayer() {
        LogHelper.d(TAG, "start");

        if ( STATE_STOP != mState ) {
            mPlayer.stop();

            LogHelper.d(TAG, "start jon playthread");
        }

        LogHelper.d(TAG, "end");
    }

    @Override
    public void freeEncodeBuffer(BufferData buffer) {
        mBufferQueue.putFull(buffer);
    }

    @Override
    public BufferData getEncodeBuffer() {
        return mBufferQueue.getEmpty();
    }

    @Override
    public BufferData getPlayBuffer() {
        return mBufferQueue.getFull();
    }

    @Override
    public void freePlayData(BufferData data) {
        mBufferQueue.putEmpty(data);
    }

    @Override
    public void onPlayStart() {
        LogHelper.d(TAG, "onPlayStart");
    }

    @Override
    public void onPlayStop() {
        LogHelper.d(TAG, "onPlayStop");
        stop();
    }

    @Override
    public void onBeginEncode() {
        LogHelper.d(TAG, "onBeginGen");
    }

    @Override
    public void onFinishEncode() {
        synchronized (this) {
            if ( null != mEncodeThread ) {
                mEncodeThread = null;
                this.notify();
            }
        }
        LogHelper.d(TAG, "onFinishGen");
    }

    @Override
    public void onStartEncode() {
        LogHelper.d(TAG, "onStartGen");
    }

    @Override
    public void onEndEncode() {
        LogHelper.d(TAG, "onEndcode End");
    }

    @Override
    public void onSinToken(int[] tokens) {
        if ( null != mListener && null != tokens ) {
            LogHelper.d(TAG, "onSinToken " + tokens.length);
            mListener.onSinToken(tokens);
        }
    }

}
