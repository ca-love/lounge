package com.cllive.lounge

import android.graphics.drawable.Drawable
import androidx.leanback.widget.GuidanceStylist

/**
 * Constructs a [GuidanceStylist.Guidance].
 */
@Suppress("FunctionName")
fun Guidance(
  title: String? = null,
  description: String? = null,
  breadcrumb: String? = null,
  icon: Drawable? = null,
): GuidanceStylist.Guidance = GuidanceStylist.Guidance(
  title,
  description,
  breadcrumb,
  icon,
)
