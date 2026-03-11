package com.example.zenith.presenters.home.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties


@Composable
fun OfflineBanner(isOnline: Boolean, isArabic: Boolean) {
    AnimatedVisibility(
        visible = !isOnline,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            color = Color.Red.copy(alpha = 0.8f),
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Rounded.WifiOff,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isArabic) "أنت الآن في وضع عدم الاتصال" else "You are offline",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun NoInternetDialog(isArabic: Boolean, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1A1C1E),
                            Color(0xFF2C3E50)
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(50.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Rounded.WifiOff,
                        contentDescription = null,
                        tint = Color.Cyan,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = if (isArabic) "عذراً! لا يوجد إنترنت" else "Oops! No Internet",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (isArabic) 
                        "هذه الخاصية تتطلب اتصالاً بالإنترنت. يرجى التحقق من الشبكة والمحاولة مرة أخرى." 
                        else "This feature requires an active internet connection. Please check your network and try again.",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Cyan,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (isArabic) "حسناً" else "Got it",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
