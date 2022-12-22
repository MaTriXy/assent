/**
 * Designed and developed by Aidan Follestad (@afollestad)
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
package com.afollestad.assent.sample

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.afollestad.assent.Permission.READ_CONTACTS
import com.afollestad.assent.Permission.READ_SMS
import com.afollestad.assent.Permission.WRITE_EXTERNAL_STORAGE
import com.afollestad.assent.coroutines.awaitPermissionsResult
import com.afollestad.assent.rationale.createSnackBarRationale
import com.afollestad.assent.sample.fragment.FragmentSampleActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import reactivecircus.flowbinding.android.view.clicks

/** @author Aidan Follestad (afollestad) */
@FlowPreview
@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity() {
  private val rootView by lazy { findViewById<View>(R.id.rootView) }
  private val requestPermissionButton by lazy { findViewById<View>(R.id.requestPermissionButton) }
  private val statusText by lazy { findViewById<TextView>(R.id.statusText) }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val rationaleHandler = createSnackBarRationale(rootView) {
      onPermission(READ_CONTACTS, "Test rationale #1, please accept!")
      onPermission(WRITE_EXTERNAL_STORAGE, "Test rationale #2, please accept!")
      onPermission(READ_SMS, "Test rationale #3, please accept!")
    }

    requestPermissionButton.clicks()
      .debounce(200L)
      .onEach {
        val result = awaitPermissionsResult(
          READ_CONTACTS,
          WRITE_EXTERNAL_STORAGE,
          READ_SMS,
          rationaleHandler = rationaleHandler
        )
        statusText.text = result.toString()
      }
      .launchIn(lifecycleScope)
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.main, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if (item.itemId == R.id.fragment) {
      startActivity<FragmentSampleActivity>()
      return true
    }
    return super.onOptionsItemSelected(item)
  }
}
