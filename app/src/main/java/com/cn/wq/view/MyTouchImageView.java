package com.cn.wq.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.GestureDetector.OnDoubleTapListener;
import android.widget.ImageView;

public class MyTouchImageView extends ImageView {
	private float x_down = 0;
	private float y_down = 0;
	private PointF mid = new PointF();
	private float oldDist = 1f;
	private float oldRotation = 0;//�ڶ�����ָ����ʱ���������ת�Ƕ�
	private float rotation = 0;//��ת�ǶȲ�ֵ
	private float newRotation = 0;
	private float Reset_scale = 1;
	private Matrix matrix = new Matrix();
	private Matrix matrix1 = new Matrix();
	private Matrix savedMatrix = new Matrix();
	private GestureDetector gestureDetector;

	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	private int mode = NONE;
	private boolean isFangda = false;//˫���Ŵ�����С
	private int widthScreen;
	private int heightScreen;
//	private Bitmap gintama;
	boolean isCheckTopAndBottom,isCheckRightAndLeft;
	public MyTouchImageView(Context context) {
		super(context);
		init(context);
	}

	public MyTouchImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public MyTouchImageView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
    	/**
    	 * ������������Ƶķ���
    	 * �������ƺʹ����¼�ͬʱʹ�õķ���
    	 */
   		if (gestureDetector.onTouchEvent(event)) {
   			return true;
   		}else {
   			switch (event.getAction() & MotionEvent.ACTION_MASK) {
   			case MotionEvent.ACTION_DOWN:
   				mode = DRAG;
   				x_down = event.getX();
   				y_down = event.getY();
   				/**
   				 * ������ָ���£����ȱ���ͼƬ�����ž���savedMatrix
   				 */
   				savedMatrix.set(matrix);
   				break;
   			case MotionEvent.ACTION_POINTER_DOWN:
   				mode = ZOOM;
   				/**
   				 * �ڶ�����ָ�շ���ʱ
   				 * ����������ָ��ľ���
   				 */
   				oldDist = spacing(event);
   				/**
   				 * �ڶ�����ָ�շ���ʱ
   				 * ����������ָ������ת�Ƕ�
   				 */
   				oldRotation = rotation(event);
   				savedMatrix.set(matrix);
   				/**
   				 * �ڶ�����ָ�շ���ʱ
   				 * ����������ָ�����м�����꣬������mid��
   				 */
   				midPoint(mid, event);
   				break;
   			case MotionEvent.ACTION_MOVE:
   				if (mode == ZOOM) {
   					matrix1.set(savedMatrix);
   					/**
   					 * ������ָ��ʼ�ƶ�
   					 * �����ƶ�����ת�Ƕ�
   					 */
   					newRotation = rotation(event);
   					/**
   					 * �����Ƕ�֮��
   					 * ����ͼƬ����ת�Ƕ�
   					 */
   					rotation = newRotation - oldRotation;
   					/**
   					 * �����ƶ����������м��
   					 */
   					float newDist = spacing(event);
   					/**
   					 * �����м����̼�ʱ�Ŵ���
   					 */
   					float scale = newDist / oldDist;
   					/**
   					 * �Ŵ����ĵ������ǻ�ԭͼƬԭ����С�ı���
   					 */
   					Reset_scale = oldDist/newDist;
   					matrix1.postScale(scale, scale, mid.x, mid.y);// �s��
   					matrix1.postRotate(rotation, mid.x, mid.y);// ���D
   					matrix.set(matrix1);
   					/**
   					 * ���ø÷�����������ͼƬ
   					 */
   					this.setImageMatrix(matrix);
   				} else if (mode == DRAG) {
   					matrix1.set(savedMatrix);
   					float tx = event.getX() - x_down;
   					float ty = event.getY() - y_down;
   					/**
   					 * ������ָ�ƶ���ľ������20 �������ƶ�
   					 */
   					if (Math.sqrt(tx*tx+ty*ty)>20f) {
   						/**
   						 * ����ͼƬ�������Ļ��ߵĴ�С��boolean���͵�ֵ
   						 */
   						isCheckRightAndLeft = isCheckTopAndBottom = true;
   						/**
   						 * �õ�ĿǰͼƬ�Ŀ��
   						 */
   						RectF rectF = getMatrixRectF();
	   					/**
	   					 * ͼƬ���С����Ļ��С
	   					 * ���ƶ�
	   					 */
   						if (rectF.width()<widthScreen) {
							tx = 0;
							isCheckRightAndLeft = false;
						}
   						/**
   						 * ͼƬ�߶�С����Ļ�߶�
   						 * ���ƶ�
   						 */
   						if (rectF.height()<heightScreen) {
							ty = 0;
							isCheckTopAndBottom = false;
						}
   						matrix1.postTranslate(tx,ty);// ƽ��
   						/**
   						 * ��������϶�ͼƬ��ͬʱ���ͼƬ��Ե�Ƿ�
   						 * ������Ļ�ı�Ե����ȡ�������ע��
   						 */
//   					matrix.set(matrix1);
//   					checkDxDyBounds();
   						matrix.set(matrix1);
   						this.setImageMatrix(matrix);
   					}
   				}
   				break;
   			case MotionEvent.ACTION_UP:
   			case MotionEvent.ACTION_POINTER_UP:
   				if (mode == ZOOM) {
   					/**
   					 * ˫�ַſ���ֹͣͼƬ����ת������
   					 * Reset_scale��ԭͼƬ�����ű���
   					 */
   					matrix1.postScale(Reset_scale, Reset_scale, mid.x, mid.y);
   					/**
   					 * ˫�ַſ���ֹͣ���š���תͼƬ����ʱ��������ת�ĽǶ�
   					 * ���㻹ԭͼƬ�ĽǶȣ����յ�Ч���ǰ�ͼƬ��ֱ���ƽ������
   					 */
   					setRotate();
   					matrix.set(matrix1);
   					/**
   					 * ��ͼƬ������Ļ�м�λ��
   					 */
   					center(true, true);
   					this.setImageMatrix(matrix);
   					matrix1.reset();
   				}else if (mode == DRAG) {
   					/**
   					 * �����϶�ͼƬ���ſ���ָ��ֹͣ�϶�
   					 * ��ʱ���ͼƬ�Ƿ��Ѿ�ƫ����Ļ��Ե
   					 * ���ƫ����Ļ��Ե����ͼƬ�ص�
   					 */
   					checkDxDyBounds();
   					matrix.set(matrix1);
   					this.setImageMatrix(matrix);
   					matrix1.reset();
   				}
   				mode = NONE;
   				break;
   			}
   			return true;
   		}
	}
	private void init(final Context context){
//		gintama = BitmapFactory.decodeResource(getResources(), R.drawable.abc);
//        this.setImageBitmap(gintama);
        /**
         * ʹ��ͼƬ�ľ������ͽ���ͼƬ�����ã���������
         * setScaleType(ScaleType.MATRIX);
         * ������˵�������˸�����֮����ô����ͼƬ������λ���ϣ�
         * �ҵĽ����������ͨ��������ƣ���ͼƬ��������Ļ����
         * ���������ο� center(true,true);����
         */
        this.setScaleType(ScaleType.MATRIX);
        DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		//��ȡ��Ļ�Ŀ�͸�
		widthScreen = dm.widthPixels;
		heightScreen = dm.heightPixels;
		//��ʼ��ͼƬ�ľ���
		matrix.set(this.getImageMatrix());
		/**
		 * ��ʼ������
		 * ����  ˫�� ����
		 * ��������������������
		 * �����Ҫ�������������н��к��ֲ�����
		 * ��������ڶ�Ӧ�ķ����м��ɡ�
		 */
		gestureDetector = new GestureDetector(
				context,
				new GestureDetector.SimpleOnGestureListener(){
			@Override
			public boolean onDown(MotionEvent e) {
				return false;
			}
			@Override
			public void onLongPress(MotionEvent e) {
				super.onLongPress(e);
			}
		});
		gestureDetector.setOnDoubleTapListener(new OnDoubleTapListener() {
			@Override
			public boolean onDoubleTapEvent(MotionEvent e) {
				return false;
			}
			/**
			 * ˫�����Ƶ�ʱ�� �ᴥ���÷���һ��
			 * ����˫�����ƶ�Ӧ�Ĵ���Ӧ��������
			 */
			@Override
			public boolean onDoubleTap(MotionEvent e) {
				mid.x=e.getX();
				mid.y=e.getY();
				matrix1.set(savedMatrix);
				if (isFangda) {
					matrix1.postScale(0.5f, 0.5f, mid.x, mid.y);//��С 
					isFangda = false;
				}else {
					matrix1.postScale(2f, 2f, mid.x, mid.y);// �Ŵ�
					isFangda = true;
				}
				matrix.set(matrix1);
				center(true, true);
				MyTouchImageView.this.setImageMatrix(matrix);
				return true;
			}
			/**
			 * �������� �ᴥ���÷���һ��
			 * ������Ӧ�Ĵ���Ӧ��������
			 */
			@Override
			public boolean onSingleTapConfirmed(MotionEvent e) {
				//���ͼƬ ����activity
				((Activity)context).finish();
				return false;
			}
		});
		/**
		 * ��ʼ�� ��ͼƬ������Ļ����λ��
		 */
		center(true, true);
		/**
		 * ͼƬ��������֮����������ͼƬ�����ž���
		 */
		this.setImageMatrix(matrix);
	}
	
	/**
     * �������� ͼƬ����
     */
    protected void center(boolean horizontal, boolean vertical) {
        RectF rect = getMatrixRectF();
        float deltaX = 0, deltaY = 0;
        float height = rect.height();
        float width = rect.width();
        if (vertical) {
            /**
             *  ͼƬС����Ļ��С���������ʾ��
             *  ������Ļ�����ͼƬ�Ϸ������������ƣ�
             *  ͼƬ�·�������������
             */
            int screenHeight = heightScreen;
            if (height < screenHeight) {
                deltaY = (screenHeight - height) / 2 - rect.top;
            } else if (rect.top > 0) {
                deltaY = -rect.top;
            } else if (rect.bottom < screenHeight) {
                deltaY = this.getHeight() - rect.bottom;
            }
        }

        if (horizontal) {
            int screenWidth = widthScreen;
            if (width < screenWidth) {
                deltaX = (screenWidth - width) / 2 - rect.left;
            } else if (rect.left > 0) {
                deltaX = -rect.left;
            } else if (rect.right < screenWidth) {
                deltaX = screenWidth - rect.right;
            }
        }
        matrix.postTranslate(deltaX, deltaY);
    }
    /** 
     * ���ݵ�ǰͼƬ��Matrix���ͼƬ�ķ�Χ 
     * �����ȡ���ǵ�ǰ��ʾ��ͼƬ�Ĵ�С��
     * ͼƬ�Ŵ�󣬻�ȡ�ľ���ͼƬ�Ŵ���ͼƬ�Ĵ�С��
     * ͼƬ��С�󣬻�ȡ�ľ���ͼƬ��С���ͼƬ�Ĵ�С��
     * 
     * �����С��ͼƬ�Ĵ�С��������ġ�
     * �����ǹ̶��÷�����ס���ɡ�
     * @return 
     */  
    private RectF getMatrixRectF()
    {  
        Matrix m = matrix;
        RectF rect = new RectF();
        Drawable d = this.getDrawable();
        if (null != d)
        {  
            rect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            m.mapRect(rect);
        }
        //�ڶ��ַ���  �����ַ������ɣ�
//        Matrix m = new Matrix();
//        m.set(matrix);
//        RectF rect = new RectF(0, 0, gintama.getWidth(), gintama.getHeight());
//        m.mapRect(rect);
        return rect;
    } 
    
 // ������������
  	private float spacing(MotionEvent event) {
  		float x = event.getX(0) - event.getX(1);
  		float y = event.getY(0) - event.getY(1);
  		return (float) Math.sqrt(x * x + y * y);
  	}
  	
  	// ȡ�������ĵ�
  	private void midPoint(PointF point, MotionEvent event) {
  		float x = event.getX(0) + event.getX(1);
  		float y = event.getY(0) + event.getY(1);
  		point.set(x/2, y/2);
  	}

  	// ȡ��ת�Ƕ�
  	private float rotation(MotionEvent event) {
  		double delta_x = (event.getX(0) - event.getX(1));
  		double delta_y = (event.getY(0) - event.getY(1));
  		/**
  		 * �����к���
  		 * �����������������нǶ�
  		 */
  		double radians = Math.atan2(delta_y, delta_x);
  		return (float)(Math.toDegrees(radians));
  	}
  	/**
  	 * ��ָ�ɿ���ȷ����ת�ĽǶ�
  	 */
  	private void setRotate(){
  		if (rotation<-315) {
  				matrix1.postRotate(-360-rotation, mid.x, mid.y);// ���D
 			}else if (rotation < -270) {
 				matrix1.postRotate(-270-rotation, mid.x, mid.y);// ���D
 			}else if (rotation < -225) {
 				matrix1.postRotate(-270-rotation, mid.x, mid.y);// ���D
 			}else if (rotation < -180) {
 				matrix1.postRotate(-180-rotation, mid.x, mid.y);// ���D
 			}else if (rotation < -135) {
 				matrix1.postRotate(-180-rotation, mid.x, mid.y);// ���D
 			}else if (rotation < -90) {
 				matrix1.postRotate(-90-rotation, mid.x, mid.y);// ���D
 			}else if (rotation < -45) {
 				matrix1.postRotate(-90-rotation, mid.x, mid.y);// ���D
 			}else if (rotation < 0) {
 				matrix1.postRotate(0-rotation, mid.x, mid.y);// ���D
 			}else if (rotation < 45) {
 				matrix1.postRotate(0-rotation, mid.x, mid.y);// ���D
 			}else if (rotation < 90) {
 				matrix1.postRotate(90-rotation, mid.x, mid.y);// ���D
 			}else if (rotation < 135) {
 				matrix1.postRotate(90-rotation, mid.x, mid.y);// ���D
 			}else if (rotation < 180) {
 				matrix1.postRotate(180-rotation, mid.x, mid.y);// ���D
 			}else if (rotation < 225) {
 				matrix1.postRotate(180-rotation, mid.x, mid.y);// ���D
 			}else if (rotation < 270) {
 				matrix1.postRotate(270-rotation, mid.x, mid.y);// ���D
 			}else if (rotation < 315) {
 				matrix1.postRotate(270-rotation, mid.x, mid.y);// ���D
 			}else if (rotation < 360) {
 				matrix1.postRotate(360-rotation, mid.x, mid.y);// ���D
 			}
  	}
  	/**
  	 * ���ͼƬƫ����Ļ���ߵľ���
  	 * Ȼ��ƽ�ƣ���ͼƬ��Ե����Ļ�ߣ�
  	 * ʹͼƬ��Χû�пհ�
  	 */
 	private void checkDxDyBounds(){
 		RectF rectF = getMatrixRectF();
 		float dx =0.0f,dy=0.0f;
 		/**
 		 * ���ͼƬ���������㣬˵��ͼƬ�������
 		 * ƫ���������Ļ��������ƫ��ľ���.
 		 * rectF.left��ֵ���ǻ�������������ġ�
 		 * ͼƬ��������£���ֵΪ0.
 		 * ��ͼƬ���Ҳ��϶��Ժ󣬸�ֵ����0.
 		 * ��ͼƬ������϶��Ժ󣬸�ֵС��0.
 		 */
 		if (rectF.left>0&&isCheckRightAndLeft) {
 			dx = -rectF.left;
 		}
 		/**
 		 * ���ͼƬ���Ҳ�ƫ����Ļ���Ҳ࣬��
 		 * ͼƬ����ͼƬ�Ŀ����ͼƬ��ʾ�Ŀ�ȵĲ�.
 		 * 
 		 * rectF.right��ֵ���ǻ���������ģ�ͼƬû��������ת����£�
 		 * ��ֵ==touchImageView.getWidth()ͼƬ�Ŀ�ȡ�
 		 * ���϶�ͼƬ�Ժ󣬸�ֵ�仯��������ʾ��ͼƬ�Ŀ��
 		 */
 		if (rectF.right<this.getWidth()&&isCheckRightAndLeft) {
 			dx=this.getWidth()-rectF.right;
 		}
 		/**
 		 * ��ͼƬ��������0��˵��ͼƬ����ƫ����Ļ������
 		 * ��ͼƬ���ϻص�ƫ��ľ��롣
 		 * 
 		 * rectF.top��ֵ���ڶ������꣬
 		 * ͼƬ��������£���ֵ=0.
 		 */
 		if (rectF.top>0&&isCheckTopAndBottom) {
 			dy=-rectF.top;
 		}
 		/**
 		 * ��ͼƬ�ײ�С��ͼƬ�߶�ʱ��ͼƬƫ����Ļ�ײ�
 		 * ��ͼƬ�ص�ͼƬ�ĸ߶�����ʾ��ͼƬ�ĸ߶�֮�
 		 * 
 		 * rectF.bottom��ֵ�����ڶ������ꡣ
 		 * ͼƬ��������£���ֵ=ͼƬ�ĸ߶ȡ�
 		 */
 		if (rectF.bottom<this.getHeight()&&isCheckTopAndBottom) {
 			dy=this.getHeight()-rectF.bottom;
 		}
 		/**
 		 * ���������ͼƬ�ص�
 		 */
 		matrix1.postTranslate(dx, dy);
 	}

}
