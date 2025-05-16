package com.project.cataxi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.cataxi.database.ApiClient
import com.project.cataxi.database.auth.AuthResponse
import com.project.cataxi.database.orders.OrderResponse
import com.project.cataxi.database.orders.OrdersGetRequest
import com.project.cataxi.database.orders.OrdersResponse
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

class AccountActivity : ComponentActivity() {
    private object user {
        var firstName = mutableStateOf("")
        var secondName = mutableStateOf("")
        val email = mutableStateOf("")
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
            user.email.value = userViewModel.email.collectAsState(initial = "").value

            CaTaxiTheme (darkTheme = isDarkTheme, dynamicColor = false){
                AccountScreen(isDarkTheme, themeViewModel, userViewModel)
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AccountScreen(darkTheme: Boolean, themeViewModel: ThemeViewModel, userViewModel: UserViewModel){
        val context = LocalContext.current
        var firstName by rememberSaveable { mutableStateOf("") }
        var secondName by rememberSaveable { mutableStateOf("") }
        var orders by remember { mutableStateOf(emptyList<OrderResponse>()) }
        val call = ApiClient.ordersApi.get(OrdersGetRequest(user.email.value))

        call.enqueue(object: Callback<OrdersResponse> {
            override fun onResponse(call: Call<OrdersResponse>, response: Response<OrdersResponse>
            ) {
                if (response.isSuccessful){
                    orders = orders.toMutableList().apply { clear() }
                    for (item in response.body()?.orders!!){
                        orders = orders.toMutableList().apply { add(item) }
                    }
                }
                else{
                    Toast.makeText(
                        context,
                        "Ошибка",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<OrdersResponse>, t: Throwable) {
                Toast.makeText(
                    context,
                    "Ошибка сети: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

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
                        .padding(16.dp)
                        .fillMaxWidth()
                        .fillMaxHeight(0.85f)
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

                    Spacer(Modifier.size(10.dp))
                    Box(Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.secondary)
                        .padding(9.dp)
                    ){
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            items(orders) { item ->
                                Card(
                                    modifier = Modifier.fillMaxWidth().padding(6.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "Заказ #${item.id}",
                                            style = MaterialTheme.typography.labelSmall
                                        )


                                        RouteSection(
                                            pointA = item.pointA,
                                            pointB = item.pointB
                                        )

                                        OrderStatusBadge(
                                            isClosed = item.closedAt != null,
                                            closeDate = item.closedAt
                                        )
                                    }
                                }
                            }
                        }
                    }
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

    @Composable
    private fun RouteSection(pointA: String, pointB: String) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            Text(
                text = "Маршрут",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 4.dp))

            // Точка А
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = pointA, style = MaterialTheme.typography.bodyMedium)
            }

            // Разделитель
            Box(
                modifier = Modifier
                    .padding(start = 5.dp, top = 2.dp, bottom = 2.dp)
                    .width(2.dp)
                    .height(20.dp)
                    .background(MaterialTheme.colorScheme.outline)
            )

            // Точка B
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            color = MaterialTheme.colorScheme.tertiary,
                            shape = CircleShape
                        )
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = pointB, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }

    @Composable
    private fun OrderStatusBadge(isClosed: Boolean, closeDate: String?) {
        val statusText = if (isClosed) {
            "Завершен ${closeDate}"
        } else {
            "Активен"
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = statusText,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }

    // Форматирование даты (псевдокод - замените на реальное форматирование)
    private fun formatDate(dateString: String?): String {
        return dateString?.take(10) ?: "N/A"
    }
}
