package com.project.cataxi

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.cataxi.ui.theme.CaTaxiTheme
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.observeOn

class AccountActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settingsDataStore = SettingsDataStore(this)
        val viewModelFactory = ThemeViewModelFactory(settingsDataStore)

        setContent {
            val themeViewModel: ThemeViewModel = viewModel(factory = viewModelFactory)
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState(initial = false)

            CaTaxiTheme (darkTheme = isDarkTheme, dynamicColor = false){
                AccountScreen(isDarkTheme, themeViewModel)
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AccountScreen(darkTheme: Boolean, themeViewModel: ThemeViewModel){

        Box(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)) {
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
                        .background(MaterialTheme.colorScheme.primary)
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

                Text("ФИО", fontSize = 30.sp, color = MaterialTheme.colorScheme.onBackground)

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
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        ),
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
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                        )
                        ,
                        shape = RoundedCornerShape(
                            topStart = 0.dp,
                            topEnd = 0.dp
                        )
                    )
                    TextField(
                        value = "",
                        onValueChange = {},
                        label = { Text("Отчество") },
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                        ,
                        shape = RoundedCornerShape(
                            topStart = 0.dp,
                            topEnd = 0.dp
                        )
                    )
                    Button(
                        onClick = { startActivity(Intent(this@AccountActivity, AuthorizationActivity::class.java)) },
                        modifier = Modifier
                            .height(56.dp)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(bottomEnd = 12.dp, bottomStart = 12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Выйти", color = Color.White)
                    }

                    Spacer(Modifier.size(10.dp))

                    Toggle(label = "Темная тема", value = darkTheme, modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.secondary)
                        .height(56.dp),
                        onCheckedChange = {
                            themeViewModel.toggleTheme()
                        }
                    )
                }
            }
        }
    }

    @Composable
    fun Toggle(modifier: Modifier, label: String, value: Boolean, onCheckedChange: (Boolean) -> Unit) {
        Box (modifier
            .padding(15.dp)) {
            Text(label, modifier = Modifier.align(Alignment.CenterStart))

            Switch(
                checked = value,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.align(Alignment.CenterEnd),
                colors = SwitchDefaults.colors(
                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                    uncheckedTrackColor = MaterialTheme.colorScheme.inversePrimary
                )
            )
        }
    }

    @Composable
    fun BackButton(modifier: Modifier = Modifier) {
        Box(
            modifier = modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
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
