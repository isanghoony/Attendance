package com.ddd.attendance.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ddd.attendance.R
import com.ddd.attendance.ui.theme.DDD_NEUTRAL_BLUE_40
import com.ddd.attendance.ui.theme.DDD_NEUTRAL_GRAY_90
import com.ddd.attendance.ui.theme.DDD_WHITE

@Composable
fun DDDSelector(
    text: String,
    selected: Boolean = false,
    onClick: (Boolean) -> Unit = {},
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = DDD_NEUTRAL_GRAY_90,
                shape = RoundedCornerShape(16.dp)
            )
            .then(
                if (selected) Modifier.border(
                    width = 2.dp,
                    color = DDD_NEUTRAL_BLUE_40,
                    shape = RoundedCornerShape(16.dp)
                ) else Modifier
            )
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .clickable { onClick(true) }

    ) {
        Text(
            text = text,
            modifier = Modifier.align(Alignment.CenterStart),
            fontSize = 18.sp,
            color = DDD_WHITE
        )
        Icon(
            painter = painterResource(id = if (selected) R.drawable.ic_40_add else R.drawable.ic_40_logo),
            contentDescription = "",
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}

@Composable
@Preview
fun DDDSelectorPreview() {
    DDDSelector(
        text = "Product Manager",
        onClick = {}
    )
}