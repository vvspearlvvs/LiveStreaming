/*
 * Copyright (C) 2011-2015 GUIGUI Simon, fyhertz@gmail.com
 *
 * This file is part of libstreaming (https://github.com/fyhertz/libstreaming)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.wearable.camera6.Rtp;

import android.annotation.SuppressLint;
import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaFormat;
import android.provider.Settings;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
/**
 * An InputStream that uses data from a MediaCodec.
 * The purpose of this class is to interface existing RTP packetizers of
 * libstreaming with the new MediaCodec API. This class is not thread safe !  
 */
@SuppressLint("NewApi")
public class MediaCodecInputStream extends InputStream {

	public final String TAG = "MediaCodecInputStream"; 

	private MediaCodec mMediaCodec = null; //mEncoder이랑 같은거
	private BufferInfo mBufferInfo = new BufferInfo();
	private ByteBuffer[] mBuffers = null;
	private ByteBuffer mBuffer = null; //encodeData랑 같은거
	private int mIndex ; //encoderstatus랑 같은거
	private boolean mClosed = false;
	
	public MediaFormat mMediaFormat;

	public MediaCodecInputStream(MediaCodec mediaCodec) {
		mMediaCodec = mediaCodec;
		mBuffers = mMediaCodec.getOutputBuffers();
	}

	@Override
	public void close() {
		mClosed = true;
	}

	@Override
	public int read() throws IOException {
		return 0;
	}

	@Override
	public int read(byte[] buffer, int offset, int length) throws IOException {
		int min = 0;

		try {
			if (mBuffer==null) {
				while (!Thread.interrupted() && !mClosed) {
					mIndex = mMediaCodec.dequeueOutputBuffer(mBufferInfo, 1000000);
					if (mIndex>=0 ){
						//Log.d(TAG,"Index: "+mIndex+" Time: "+mBufferInfo.presentationTimeUs+" size: "+mBufferInfo.size);
						mBuffer = mBuffers[mIndex];
						Log.i(TAG,"인코딩한 버퍼 데이터"+mBuffer);
						Log.i(TAG,"인코딩한 데이터 전송중"+new byte[mBuffer.remaining()].length);

						mBuffer.position(mBufferInfo.offset); //원래0
                        mBuffer.limit(mBufferInfo.offset+mBufferInfo.size);
						break;

					} else if (mIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
						mBuffers = mMediaCodec.getOutputBuffers();
					} else if (mIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
						mMediaFormat = mMediaCodec.getOutputFormat();
						Log.i(TAG,mMediaFormat.toString());
					} else if (mIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
						Log.i(TAG,"다시시작 No buffer available..."+mIndex);
						//return 0;
					} else {
						Log.e(TAG,"Message: "+mIndex);
						//return 0;
					}
				}			
			}
			
			if (mClosed) throw new IOException("This InputStream was closed");

			min = length < mBufferInfo.size - mBuffer.position() ? length : mBufferInfo.size - mBuffer.position();
			if(buffer.length>mBuffer.capacity()){
				Log.i(TAG,"버퍼크기 다시 조정");
				mBuffer = ByteBuffer.allocate(length);
			}
			mBuffer.get(buffer, offset, min);
			if (mBuffer.position()>=mBufferInfo.size) {
				mMediaCodec.releaseOutputBuffer(mIndex, false);
				mBuffer = null;
			}

		} catch (RuntimeException e) {
			e.printStackTrace();
		}

		return min;
	}
	
	public int available() {
		if (mBuffer != null) 
			return mBufferInfo.size - mBuffer.position();
		else 
			return 0;
	}

	public BufferInfo getLastBufferInfo() {
		return mBufferInfo;
	}

}
