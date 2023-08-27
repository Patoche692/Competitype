package com.example.typerace.shared

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeCompilerApi
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.typerace.ui.theme.Typography

@Composable
fun LabeledIconButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String = "",
    onClick: () -> Unit = {}
) {
    LabeledIconButton(
        icon = rememberVectorPainter(image = icon),
        modifier = modifier,
        label = label,
        onClick = onClick
    )
}

@Composable
fun LabeledIconButton(
    modifier: Modifier = Modifier,
    icon: Painter,
    label: String = "",
    onClick: () -> Unit = {}
) {
    OutlinedButton(
        onClick = onClick,
    ) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = icon,
                modifier = Modifier.size(25.dp),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                modifier = Modifier.padding(vertical = 5.dp),
                text = label,
                style = Typography.bodyLarge
            )
        }
    }
}

@Composable
fun SmallLabeledIconButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    onClick: () -> Unit = {},
    tint: Color = LocalContentColor.current
) {
    SmallLabeledIconButton(
        icon = rememberVectorPainter(image = icon),
        label = label,
        modifier = modifier,
        onClick = onClick,
        tint = tint
    )
}


@Composable
fun SmallLabeledIconButton(
    modifier: Modifier = Modifier,
    icon: Painter,
    label: String,
    onClick: () -> Unit = {},
    tint: Color = LocalContentColor.current
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            contentColor = MaterialTheme.colorScheme.onBackground,
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
        )
    ) {
        Column(
            modifier = modifier.padding(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                modifier = Modifier.size(30.dp),
                tint = tint,
                contentDescription = null
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = Typography.labelLarge
            )
        }
    }
}

@Composable
fun MinimalLabeledIconButton(
    modifier: Modifier = Modifier,
    icon: Painter,
    label: String,
    onClick: () -> Unit = {},
    tint: Color = LocalContentColor.current
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.background, contentColor = MaterialTheme.colorScheme.onBackground)
    ) {
        Column(
            //modifier = modifier.padding(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                modifier = Modifier.size(30.dp),
                tint = tint,
                contentDescription = null
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = Typography.labelMedium
            )
        }
    }
}
@Composable
fun MinimalLabeledIconButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    onClick: () -> Unit = {},
    tint: Color = LocalContentColor.current
) {
    MinimalLabeledIconButton(
        icon = rememberVectorPainter(image = icon),
        label = label,
        modifier = modifier,
        onClick = onClick,
        tint = tint
    )
}
