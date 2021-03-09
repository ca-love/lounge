/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * For from airbnb/epoxy
 */
package jp.co.cyberagent.lounge.paging.util

import androidx.leanback.widget.Presenter
import androidx.recyclerview.widget.DiffUtil
import jp.co.cyberagent.lounge.LoungeModel

/**
 * Dummy item for testing.
 */
data class Item(val id: Int, val value: String) {
  companion object {
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Item>() {
      override fun areItemsTheSame(oldItem: Item, newItem: Item) = oldItem.id == newItem.id

      override fun areContentsTheSame(oldItem: Item, newItem: Item) = oldItem == newItem
    }
  }
}

class FakePlaceholderModel(val pos: Int) : LoungeModel {
  override val key = -pos.toLong()
  override val presenter: Presenter = EmptyPresenter
}

class FakeModel(val item: Item) : LoungeModel {
  override val key = item.id.toLong()
  override val presenter: Presenter = EmptyPresenter
}
