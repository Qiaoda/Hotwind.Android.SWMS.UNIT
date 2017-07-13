package cn.jitmarketing.hot.view;

import cn.jitmarketing.hot.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * 自定义控件实现ip地址特殊输入
 * 
 * @author 子墨
 * 
 *         2015-1-4
 */
public class SkuEditText extends LinearLayout {

	private EditText mFirstIP;
	private EditText mSecondIP;
	private EditText mThirdIP;
	private EditText mFourthIP;

	private String mText;
	private String mText1;
	private String mText2;
	private String mText3;
	private String mText4;

	private SharedPreferences mPreferences;

	public SkuEditText(Context context, AttributeSet attrs) {
		super(context, attrs);

		/**
		 * 初始化控件
		 */
		View view = LayoutInflater.from(context).inflate(
				R.layout.custom_edittext_sku, this);
		mFirstIP = (EditText)findViewById(R.id.sku_first);
		mSecondIP = (EditText)findViewById(R.id.sku_second);
		mThirdIP = (EditText)findViewById(R.id.sku_third);
		mFourthIP = (EditText)findViewById(R.id.sku_fourth);

		mPreferences = context.getSharedPreferences("config_IP",
				Context.MODE_PRIVATE);

		OperatingEditText(context);
	}

	/**
	 * 获得EditText中的内容,当每个Edittext的字符达到三位时,自动跳转到下一个EditText,当用户点击.时,
	 * 下一个EditText获得焦点
	 */
	private void OperatingEditText(final Context context) {
		mFirstIP.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if(s != null) {
					mText1 = s.toString().trim().toUpperCase();
				}
				/**
				 * 获得EditTe输入内容,做判断,如果大于255,提示不合法,当数字为合法的三位数下一个EditText获得焦点,
				 * 用户点击啊.时,下一个EditText获得焦点
				 */
				if (s != null && s.length() > 0) {
					if (s.length() > 0 || s.toString().trim().contains(".")) {
						if (s.toString().trim().contains(".")) {
							mText1 = s.toString().substring(0, s.length() - 1).toUpperCase();
//							mFirstIP.setText(mText1);
						} else {
							mText1 = s.toString().trim().toUpperCase();
						}
						mFirstIP.removeTextChangedListener(this);//解除文字改变事件 
						mFirstIP.setText(mText1);//转换 
						mFirstIP.setSelection(mText1.length());//重新设置光标位置 
						mFirstIP.addTextChangedListener(this);//重新绑 
						mSecondIP.setFocusable(true);
						mSecondIP.requestFocus();
					}
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		mSecondIP.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if(s != null) {
					mText2 = s.toString().trim().toUpperCase();
				}
				/**
				 * 获得EditTe输入内容,做判断,如果大于255,提示不合法,当数字为合法的三位数下一个EditText获得焦点,
				 * 用户点击啊.时,下一个EditText获得焦点
				 */
				if (s != null && s.length() > 0) {
					if (s.length() > 1 || s.toString().trim().contains(".")) {
						if (s.toString().trim().contains(".")) {
							mText2 = s.toString().substring(0, s.length() - 1).toUpperCase();
//							mSecondIP.setText(mText2);
						} else {
							mText2 = s.toString().trim().toUpperCase();
						}
						mSecondIP.removeTextChangedListener(this);//解除文字改变事件 
						mSecondIP.setText(mText2);//转换 
						mSecondIP.setSelection(mText2.length());//重新设置光标位置 
						mSecondIP.addTextChangedListener(this);//重新绑 
						mThirdIP.setFocusable(true);
						mThirdIP.requestFocus();
					} else {
						mSecondIP.removeTextChangedListener(this);//解除文字改变事件 
						mSecondIP.setText(mText2);//转换 
						mSecondIP.setSelection(mText2.length());//重新设置光标位置 
						mSecondIP.addTextChangedListener(this);//重新绑 
					}
				}

				/**
				 * 当用户需要删除时,此时的EditText为空时,上一个EditText获得焦点
				 */
				if (start == 0 && s.length() == 0) {
					mFirstIP.setFocusable(true);
					mFirstIP.requestFocus();
//					mFirstIP.setSelection(mPreferences.getInt("IP_FIRST", 0));
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		mThirdIP.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if(s != null) {
					mText3 = s.toString().trim().toUpperCase();
				}
				/**
				 * 获得EditTe输入内容,做判断,如果大于255,提示不合法,当数字为合法的三位数下一个EditText获得焦点,
				 * 用户点击啊.时,下一个EditText获得焦点
				 */
				if (s != null && s.length() > 0) {
					if (s.length() > 1 || s.toString().trim().contains(".")) {
						if (s.toString().trim().contains(".")) {
							mText3 = s.toString().substring(0, s.length() - 1).toUpperCase();
//							mThirdIP.setText(mText3);
						} else {
							mText3 = s.toString().trim().toUpperCase();
						}
						mThirdIP.removeTextChangedListener(this);//解除文字改变事件 
						mThirdIP.setText(mText3);//转换 
						mThirdIP.setSelection(mText3.length());//重新设置光标位置 
						mThirdIP.addTextChangedListener(this);//重新绑 
						mFourthIP.setFocusable(true);
						mFourthIP.requestFocus();
					} else {
						mThirdIP.removeTextChangedListener(this);//解除文字改变事件 
						mThirdIP.setText(mText3);//转换 
						mThirdIP.setSelection(mText3.length());//重新设置光标位置 
						mThirdIP.addTextChangedListener(this);//重新绑 
					}
				}

				/**
				 * 当用户需要删除时,此时的EditText为空时,上一个EditText获得焦点
				 */
				if (start == 0 && s.length() == 0) {
					mSecondIP.setFocusable(true);
					mSecondIP.requestFocus();
//					mSecondIP.setSelection(mPreferences.getInt("IP_SECOND", 0));
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		mFourthIP.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if(s != null) {
					mText4 = s.toString().trim().toUpperCase();
				}
				/**
				 * 获得EditTe输入内容,做判断,如果大于255,提示不合法,当数字为合法的三位数下一个EditText获得焦点,
				 * 用户点击啊.时,下一个EditText获得焦点
				 */
				if (s != null && s.length() > 1) {
					mText4 = s.toString().trim().toUpperCase();

				}
				mFourthIP.removeTextChangedListener(this);//解除文字改变事件 
				mFourthIP.setText(mText4);//转换 
				mFourthIP.setSelection(mText4.length());//重新设置光标位置 
				mFourthIP.addTextChangedListener(this);//重新绑 
				/**
				 * 当用户需要删除时,此时的EditText为空时,上一个EditText获得焦点
				 */
				if (start == 0 && s.length() == 0) {
					mThirdIP.setFocusable(true);
					mThirdIP.requestFocus();
//					mThirdIP.setSelection(mPreferences.getInt("IP_THIRD", 0));
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}
	
	public boolean invalid() {
		if (TextUtils.isEmpty(mText1) || TextUtils.isEmpty(mText2)
				|| TextUtils.isEmpty(mText3) || TextUtils.isEmpty(mText4)) {
			return false;
		}
		if(mText1.equals("") || mText2.equals("") || mText3.equals("") || mText4.equals("")) {
			return false;
		}
		StringBuilder builder = new StringBuilder();		
		builder.append(mText1==null?"":mText1);
		builder.append(mText2==null?"":mText2);
		builder.append(mText3==null?"":mText3);
		builder.append(mText4==null?"":mText4);
		if(builder.toString().length()<7)
			return false;
		if(builder.toString().startsWith("E") || builder.toString().startsWith("N") || builder.toString().startsWith("T"))
			return false;
		return true;
	}

	public String getText(Context context) {
		if (TextUtils.isEmpty(mText1) || TextUtils.isEmpty(mText2)
				|| TextUtils.isEmpty(mText3) || TextUtils.isEmpty(mText4)) {
		}
		StringBuilder builder = new StringBuilder();		
		builder.append(mText1==null?"":mText1);
		builder.append(mText2==null?"":mText2);
		builder.append(mText3==null?"":mText3);
		builder.append(mText4==null?"":mText4);
		return builder.toString();
	}
	
	public void setText(String shelfCode){
		if(shelfCode.length()>0){
			mFirstIP.setText(shelfCode.substring(0, 1));
			mFirstIP.setSelection(shelfCode.substring(0, 1).length());
			mSecondIP.setText(shelfCode.substring(1, 3));
			mSecondIP.setSelection(shelfCode.substring(1, 3).length());
			mThirdIP.setText(shelfCode.substring(3, 5));
			mThirdIP.setSelection(shelfCode.substring(3, 5).length());
			mFourthIP.setText(shelfCode.substring(5, 7));
			mFourthIP.setSelection(shelfCode.substring(5, 7).length());
		} else {
			mFirstIP.setText("");
			mSecondIP.setText("");
			mThirdIP.setText("");
			mFourthIP.setText("");
			mFirstIP.setFocusable(true);
			mFirstIP.requestFocus();
		}
	}
	public void stopEdit(){
		mFirstIP.setKeyListener(null);
		mSecondIP.setKeyListener(null);
		mThirdIP.setKeyListener(null);
		mFourthIP.setKeyListener(null);
	}
	
	public void setEnable(boolean enabled) {
		mFirstIP.setEnabled(enabled);
		mSecondIP.setEnabled(enabled);
		mThirdIP.setEnabled(enabled);
		mFourthIP.setEnabled(enabled);
	}
}
