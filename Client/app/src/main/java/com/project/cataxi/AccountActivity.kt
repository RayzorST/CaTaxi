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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.cataxi.database.auth.ApiClient
import com.project.cataxi.datastore.SettingsDataStore
import com.project.cataxi.datastore.ThemeViewModel
import com.project.cataxi.datastore.ThemeViewModelFactory
import com.project.cataxi.datastore.UserDataStore
import com.project.cataxi.datastore.UserViewModel
import com.project.cataxi.datastore.UserViewModelFactory
import com.project.cataxi.ui.theme.CaTaxiTheme
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first

class AccountActivity : ComponentActivity() {
    object user {
        var firstName = mutableStateOf("")
        var secondName = mutableStateOf("")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settingsDataStore = SettingsDataStore(this)
        val viewModelFactory = ThemeViewModelFactory(settingsDataStore)

        val userDataStore = UserDataStore(this)
        val userViewModelFactory = UserViewModelFactory(userDataStore)

        setContent {
            val themeViewModel: ThemeViewModel = viewModel(factory = viewModelFactory)
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState(initial = false)

            val userViewModel: UserViewModel = viewModel(factory = userViewModelFactory)
            user.firstName.value = userViewModel.firstName.collectAsState(initial = "").value
            user.secondName.value = userViewModel.secondName.collectAsState(initial = "").value

            CaTaxiTheme (darkTheme = isDarkTheme, dynamicColor = false){
                AccountScreen(isDarkTheme, themeViewModel, userViewModel)
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AccountScreen(darkTheme: Boolean, themeViewModel: ThemeViewModel, userViewModel: UserViewModel){
        var firstName by rememberSaveable { mutableStateOf("") }
        var secondName by rememberSaveable { mutableStateOf("") }

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

                Text(user.firstName.value + " " + user.secondName.value, fontSize = 30.sp, color = MaterialTheme.colorScheme.onBackground)

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp)
                ) {
                    TextField(
                        value = firstName,
                        onValueChange = { firstName = it},
                        label = { Text("Имя") },
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
                        value = secondName,
                        onValueChange = { secondName = it },
                        label = { Text("Фамилия") },
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

                    Button(
                        onClick = {
                            //val call = ApiClient.authApi.

                            if (firstName.isNotEmpty() && firstName != user.firstName.value){

                            }

                            if (secondName.isNotEmpty() && secondName != user.secondName.value){

                            }
                        },
                        modifier = Modifier
                            .height(56.dp)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(bottomEnd = 12.dp, bottomStart = 12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Сохранить", color = Color.White)
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
                Column(
                    modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Button(
                        onClick = {
                            userViewModel.clear()
                            startActivity(Intent(this@AccountActivity, AuthorizationActivity::class.java))
                        },
                        modifier = Modifier
                            .height(56.dp)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Выйти", color = Color.White)
                    }
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
