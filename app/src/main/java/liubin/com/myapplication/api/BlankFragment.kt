package liubin.com.myapplication.api

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.mylibrary.base.ProgressFragment
import com.example.mylibrary.base.TopBarActivity
import liubin.com.myapplication.R
import timber.log.Timber

class BlankFragment : ProgressFragment<TopBarActivity>() {

  override fun getContentLayoutResourceId(): Int {
    return R.layout.fragment_blank;
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val findViewById: TextView = view?.findViewById(R.id.text) as TextView
    findViewById.setText("wokao");
    findViewById.setOnClickListener {
      tv ->
      Timber.e((tv as TextView).text as String)
      Toast.makeText(context, "wokkkk", Toast.LENGTH_LONG).show()
    }

  }

  override fun initTopBar(activity: TopBarActivity?) {
    super.initTopBar(activity)
    activity?.toolBar?.setTitle("测试啊")
    activity?.toolBar?.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
    activity?.toolBar?.setNavigationOnClickListener {
      getActivity().finish();
    }
  }

  private var mParam1: String? = null
  private var mParam2: String? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    if (arguments != null) {
      mParam1 = arguments.getString(ARG_PARAM1)
      mParam2 = arguments.getString(ARG_PARAM2)
    }
  }


  companion object {
    private val ARG_PARAM1 = "param1"
    private val ARG_PARAM2 = "param2"

    fun newInstance(param1: String, param2: String): BlankFragment {
      val fragment = BlankFragment()
      val args = Bundle()
      args.putString(ARG_PARAM1, param1)
      args.putString(ARG_PARAM2, param2)
      fragment.arguments = args
      return fragment
    }
  }
}

