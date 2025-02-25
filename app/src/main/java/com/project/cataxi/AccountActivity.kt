package com.project.cataxi

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class AccountActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AccountScreen()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AccountScreen(){
        Box(modifier = Modifier.fillMaxSize()) {
            BackButton(modifier = Modifier
                .padding(20.dp)
                .align(Alignment.TopStart))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.size(10.dp))

                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(colorResource(R.color.red))
                        .clickable {
                            startActivity(Intent(this@AccountActivity, MainActivity::class.java))
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.account),
                        contentDescription = "Аватарка пользователя",
                        modifier = Modifier.fillMaxSize().padding(5.dp)
                    )
                }

                Spacer(modifier = Modifier.size(10.dp))

                Text("ФИО", fontSize = 30.sp)

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp)
                ) {
                    TextField(
                        value = "",
                        onValueChange = {},
                        label = { Text("Фамилия") },
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = TextFieldDefaults.textFieldColors(containerColor = colorResource(R.color.white)),
                        shape = RoundedCornerShape(
                            topStart = 12.dp,
                            topEnd = 12.dp
                        )
                    )
                    TextField(
                        value = "",
                        onValueChange = {},
                        label = { Text("Имя") },
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = TextFieldDefaults.textFieldColors(containerColor = colorResource(R.color.white))
                    )
                    TextField(
                        value = "",
                        onValueChange = {},
                        label = { Text("Отчество") },
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = TextFieldDefaults.textFieldColors(containerColor = colorResource(R.color.white))
                    )
                    Button(
                        onClick = { startActivity(Intent(this@AccountActivity, AuthorizationActivity::class.java)) },
                        modifier = Modifier
                            .height(56.dp)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(bottomEnd = 12.dp, bottomStart = 12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.red))
                    ) {
                        Text("Выйти", color = Color.White)
                    }
                }
            }
        }
    }

    @Composable
    fun BackButton(modifier: Modifier = Modifier) {
        Box(
            modifier = modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(colorResource(R.color.red))
                .clickable {
                    startActivity(Intent(this@AccountActivity, MainActivity::class.java))
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.arrow_back),
                contentDescription = "Аватарка пользователя",
                modifier = Modifier.fillMaxSize().padding(5.dp)
            )
        }
    }
}