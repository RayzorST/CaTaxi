package com.project.cataxi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.project.cataxi.database.auth.ApiClient
import com.project.cataxi.database.auth.AuthResponse
import com.project.cataxi.database.auth.LoginRequest
import com.project.cataxi.database.auth.RegistrationRequest
import com.project.cataxi.datastore.SettingsDataStore
import com.project.cataxi.datastore.ThemeViewModel
import com.project.cataxi.datastore.ThemeViewModelFactory
import com.project.cataxi.datastore.UserDataStore
import com.project.cataxi.datastore.UserViewModel
import com.project.cataxi.datastore.UserViewModelFactory
import com.project.cataxi.ui.theme.CaTaxiTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthorizationActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settingsDataStore = SettingsDataStore(this)
        val viewModelFactory = ThemeViewModelFactory(settingsDataStore)

        val userDataStore = UserDataStore(this)
        val userViewModelFactory = UserViewModelFactory(userDataStore)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        setContent  {
            val themeViewModel: ThemeViewModel = viewModel(factory = viewModelFactory)
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState(initial = false)

            val userViewModel: UserViewModel = viewModel(factory = userViewModelFactory)
            val userToken by userViewModel.token.collectAsState(initial = "")

            if (userToken.isNotEmpty()){
                startActivity(Intent(this@AuthorizationActivity, MainActivity::class.java))
            }
            CaTaxiTheme (darkTheme = isDarkTheme, dynamicColor = false) {
                LoginAndRegistration(userViewModel)
            }
        }
    }


    @Composable
    fun LoginAndRegistration(userViewModel: UserViewModel) {
        val navController = rememberNavController()

        NavHost(modifier = Modifier.background(MaterialTheme.colorScheme.background),
            navController = navController, startDestination = "login_screen", builder = {
            composable(
                "login_screen",
                content = { LoginScreen(navController, userViewModel) })
            composable(
                "register_screen",
                content = { RegistrationScreen(navController, userViewModel) })
        })
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun LoginScreen(navController: NavController, userViewModel: UserViewModel) {
        val context = LocalContext.current
        var email by rememberSaveable { mutableStateOf("") }
        val emailErrorState = remember { mutableStateOf(false) }
        val passwordErrorState = remember { mutableStateOf(false) }
        var password by rememberSaveable { mutableStateOf("") }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.tertiary)) {
                    append("В")
                }
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSecondary)) {
                    append("ойти")
                }
            }, fontSize = 30.sp)
            Spacer(Modifier.size(16.dp))
            OutlinedTextField(
                value = email,
                onValueChange = {
                    if (emailErrorState.value) {
                        emailErrorState.value = false
                    }
                    email = it
                },
                isError = emailErrorState.value,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(text = "Введите почту*")
                },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedLabelColor = MaterialTheme.colorScheme.tertiary,
                    focusedBorderColor = MaterialTheme.colorScheme.tertiary
                )
            )
            if (emailErrorState.value) {
                Text(text = "Обязательно поле", color = MaterialTheme.colorScheme.tertiary)
            }
            Spacer(Modifier.size(16.dp))
            val passwordVisibility = remember { mutableStateOf(true) }
            OutlinedTextField(
                value = password,
                onValueChange = {
                    if (passwordErrorState.value) {
                        passwordErrorState.value = false
                    }
                    password = it
                },
                isError = passwordErrorState.value,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(text = "Введите пароль*")
                },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedLabelColor = MaterialTheme.colorScheme.tertiary,
                    focusedBorderColor = MaterialTheme.colorScheme.tertiary
                ),
                visualTransformation = if (passwordVisibility.value) PasswordVisualTransformation() else VisualTransformation.None
            )
            if (passwordErrorState.value) {
                Text(text = "Обязательно поле", color = MaterialTheme.colorScheme.tertiary)
            }
            Spacer(Modifier.size(16.dp))
            Button(
                shape = RoundedCornerShape(12.dp),
                onClick = {
                    when {
                        email.length < 3 || email.length > 25 -> {
                            emailErrorState.value = true
                        }

                        password.length < 3 || password.length > 25 -> {
                            passwordErrorState.value = true
                        }

                        else -> {
                            passwordErrorState.value = false
                            emailErrorState.value = false

                            val call = ApiClient.authApi.login(LoginRequest(email, password))

                            call.enqueue(object: Callback<AuthResponse> {
                                override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>
                                ) {
                                    if (response.isSuccessful){
                                        userViewModel.set(
                                            response.body()?.token.toString(),
                                            response.body()?.firstName.toString(),
                                            response.body()?.secondName.toString())
                                        Toast.makeText(
                                            context,
                                            "Успешная авторизация",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        startActivity(Intent(this@AuthorizationActivity, MainActivity::class.java))
                                    }
                                    else{
                                        Toast.makeText(
                                            context,
                                            "Ошибка входа",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                                    Toast.makeText(
                                        context,
                                        "Ошибка сети: ${t.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            })
                        }
                    }

                },
                content = {
                    Text(text = "Войти")
                },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.size(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                TextButton(onClick = {
                    navController.navigate("register_screen") {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }) {
                    Text(text = "Зарегистрироваться", color = MaterialTheme.colorScheme.tertiary)
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun RegistrationScreen(navController: NavController, userViewModel: UserViewModel) {
        val context = LocalContext.current
        var firstName by rememberSaveable { mutableStateOf("") }
        var secondName by rememberSaveable { mutableStateOf("") }
        var email by rememberSaveable { mutableStateOf("") }
        var password by rememberSaveable { mutableStateOf("") }
        var confirmPassword by rememberSaveable { mutableStateOf("") }

        val firstNameErrorState = remember { mutableStateOf(false) }
        val secondNameErrorState = remember { mutableStateOf(false) }
        val emailErrorState = remember { mutableStateOf(false) }
        val passwordErrorState = remember { mutableStateOf(false) }
        val confirmPasswordErrorState = remember { mutableStateOf(false) }
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Center,
        ) {

            Text(text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.tertiary)) {
                    append("Р")
                }
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSecondary)) {
                    append("егистрация")
                }
            }, fontSize = 30.sp)
            Spacer(Modifier.size(16.dp))

            OutlinedTextField(
                value = firstName,
                onValueChange = {
                    if (firstNameErrorState.value) {
                        firstNameErrorState.value = false
                    }
                    firstName = it
                },

                modifier = Modifier.fillMaxWidth(),
                isError = firstNameErrorState.value,
                label = {
                    Text(text = "Имя*")
                },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedLabelColor = MaterialTheme.colorScheme.tertiary,
                    focusedBorderColor = MaterialTheme.colorScheme.tertiary
                )
            )
            if (firstNameErrorState.value) {
                Text(text = "Обяазтельное поле", color = MaterialTheme.colorScheme.tertiary)
            }
            Spacer(Modifier.size(16.dp))

            OutlinedTextField(
                value = secondName,
                onValueChange = {
                    if (secondNameErrorState.value) {
                        secondNameErrorState.value = false
                    }
                    secondName = it
                },

                modifier = Modifier.fillMaxWidth(),
                isError = secondNameErrorState.value,
                label = {
                    Text(text = "Фамилия*")
                },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedLabelColor = MaterialTheme.colorScheme.tertiary,
                    focusedBorderColor = MaterialTheme.colorScheme.tertiary
                )
            )
            if (secondNameErrorState.value) {
                Text(text = "Обяазтельное поле", color = MaterialTheme.colorScheme.tertiary)
            }
            Spacer(Modifier.size(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    if (emailErrorState.value) {
                        emailErrorState.value = false
                    }
                    email = it
                },

                modifier = Modifier.fillMaxWidth(),
                isError = emailErrorState.value,
                label = {
                    Text(text = "Почта*")
                },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedLabelColor = MaterialTheme.colorScheme.tertiary,
                    focusedBorderColor = MaterialTheme.colorScheme.tertiary
                )
            )
            if (emailErrorState.value) {
                Text(text = "Обяазтельное поле", color = MaterialTheme.colorScheme.tertiary)
            }
            Spacer(Modifier.size(16.dp))


            val passwordVisibility = remember { mutableStateOf(true) }
            OutlinedTextField(
                value = password,
                onValueChange = {
                    if (passwordErrorState.value) {
                        passwordErrorState.value = false
                    }
                    password = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(text = "Пароль*")
                },
                isError = passwordErrorState.value,
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedLabelColor = MaterialTheme.colorScheme.tertiary,
                    focusedBorderColor = MaterialTheme.colorScheme.tertiary
                ),
                visualTransformation = if (passwordVisibility.value) PasswordVisualTransformation() else VisualTransformation.None
            )
            if (passwordErrorState.value) {
                Text(text = "Required", color = Color.Red)
            }

            Spacer(Modifier.size(16.dp))
            val cPasswordVisibility = remember { mutableStateOf(true) }
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    if (confirmPasswordErrorState.value) {
                        confirmPasswordErrorState.value = false
                    }
                    confirmPassword = it
                },
                modifier = Modifier.fillMaxWidth(),
                isError = confirmPasswordErrorState.value,
                label = {
                    Text(text = "Повтор пароля*")
                },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedLabelColor = MaterialTheme.colorScheme.tertiary,
                    focusedBorderColor = MaterialTheme.colorScheme.tertiary
                ),
                visualTransformation = if (cPasswordVisibility.value) PasswordVisualTransformation() else VisualTransformation.None
            )
            if (confirmPasswordErrorState.value) {
                val msg = if (confirmPassword.isEmpty()) {
                    "Обязательно поле"
                } else if (confirmPassword != password) {
                    "Разные пароли"
                } else {
                    ""
                }
                Text(text = msg, color = MaterialTheme.colorScheme.tertiary)
            }
            Spacer(Modifier.size(16.dp))
            Button(
                shape = RoundedCornerShape(12.dp),
                onClick = {
                    Log.e("efe", email.contains("@").toString())
                    when {
                        firstName.length < 3 || firstName.length > 25 -> {
                            firstNameErrorState.value = true
                        }

                        email.length < 3 || email.length > 25 || !email.contains("@") -> {
                            emailErrorState.value = true
                        }

                        password.length < 3 || password.length > 25 -> {
                            passwordErrorState.value = true
                        }

                        confirmPassword != password -> {
                            confirmPasswordErrorState.value = true
                        }

                        else -> {
                            passwordErrorState.value = false
                            emailErrorState.value = false

                            val call = ApiClient.authApi.registration(RegistrationRequest(email, password, firstName, secondName))

                            call.enqueue(object: Callback<AuthResponse> {
                                override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>
                                ) {
                                    if (response.isSuccessful){
                                        userViewModel.set(
                                            response.body()?.token.toString(),
                                            response.body()?.firstName.toString(),
                                            response.body()?.secondName.toString())
                                        Toast.makeText(
                                            context,
                                            "Успешная регистрация",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        startActivity(Intent(this@AuthorizationActivity, MainActivity::class.java))
                                    }
                                    else{
                                        Toast.makeText(
                                            context,
                                            "Ошибка регистрации",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                                    Toast.makeText(
                                        context,
                                        "Ошибка сети: ${t.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            })
                        }
                    }
                },
                content = {
                    Text(text = "Регистрация", color = Color.White)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.tertiary)
            )
            Spacer(Modifier.size(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                TextButton(onClick = {
                    navController.navigate("login_screen") {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }) {
                    Text(text = "Войти", color = MaterialTheme.colorScheme.tertiary)
                }
            }
        }
    }

}