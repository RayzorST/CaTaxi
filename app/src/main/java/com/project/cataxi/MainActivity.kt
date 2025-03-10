package com.project.cataxi

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapKitFactory.initialize(this)

        setContent {
            MapScreen()
        }
    }

    @Composable
    fun MapScreen() {
        var searchError = remember { mutableStateOf(0) }

        Box(modifier = Modifier.fillMaxSize()) {
            MapViewContainer(modifier = Modifier.fillMaxSize())

            AvatarButton(
                modifier = Modifier
                    .padding(20.dp)
                    .align(Alignment.TopStart)
            )

            BottomMenu(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(unbounded = true)
                    .align(Alignment.BottomCenter)
                    .shadow(elevation = 50.dp),
                searchError = searchError
            )

            when {
                searchError.value == 1 -> {
                    Placeholder(Modifier
                        .align(Alignment.Center),
                        onClickButton = {
                            searchError.value = 0
                        },
                        placeholder = "404",
                        searchError = searchError)
                }

                searchError.value == 2 -> {
                    Placeholder(Modifier
                        .align(Alignment.Center),
                        onClickButton = {
                            searchError.value = 0
                        },
                        placeholder = "Ошибка при поиске",
                        searchError = searchError)
                }
            }
        }
    }

    @Composable
    fun Placeholder(modifier: Modifier = Modifier, placeholder: String, onClickButton: () -> Unit = {}, searchError: MutableState<Int>) {
        Box(Modifier
            .background(Color(0x80000000))
            .fillMaxSize()
            .clickable { searchError.value = 0 }
        ) {
            Box(modifier = modifier
                .wrapContentWidth(unbounded = true)
                .wrapContentHeight(unbounded = true)
                .background(colorResource(R.color.white0), RoundedCornerShape(20))
                .shadow(100.dp)
            )
            {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = placeholder,
                        fontSize = 18.sp,
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onClickButton,
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.red))
                    ) {
                        Text("Обновить")
                    }
                }
            }
        }
    }

    @Composable
    fun MapViewContainer(modifier: Modifier = Modifier) {
        AndroidView(
            factory = { context ->
                MapView(context).apply {
                    map.move(
                        CameraPosition(
                            Point(55.751574, 37.573856),
                            11.0f,
                            0.0f,
                            0.0f
                        )
                    )
                }
            },
            modifier = modifier
        )
    }

    @Composable
    fun AvatarButton(modifier: Modifier = Modifier) {
        Box(
            modifier = modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(colorResource(R.color.red))
                .clickable {
                    startActivity(Intent(this@MainActivity, AccountActivity::class.java))
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.account),
                contentDescription = "Аватарка пользователя",
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    @Composable
    fun BottomMenu(modifier: Modifier = Modifier, searchError: MutableState<Int>) {
        val pointA = remember { mutableStateOf("") }
        val pointB = remember { mutableStateOf("") }

        Surface(
            modifier = modifier
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Text(text = "CaTaxi", color = colorResource(R.color.red))

                SearchBar("Точка А", pointA)

                SearchBar("Точка Б", pointB)

                TaxiCardPager()

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)){
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clickable {  },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.card),
                            contentDescription = "Карта оплаты",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(5.dp)
                        )
                    }

                    Button(
                        onClick = {
                            if (pointA.value == ""){
                                searchError.value = 1
                            }
                            else{
                                searchError.value = 2
                            }
                                  },
                        modifier = Modifier
                            .height(56.dp)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.red))
                    ) {
                        Text("Заказать", color = Color.White)
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SearchBar(
        hint: String,
        searchText: MutableState<String>,
        modifier: Modifier = Modifier
    ) {
        val keyboardController = LocalSoftwareKeyboardController.current

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            OutlinedTextField(
                value = searchText.value,
                onValueChange = { searchText.value = it },
                modifier = modifier
                    .weight(1f),
                label = { Text(text = hint) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { keyboardController?.hide() }
                ),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedLabelColor = colorResource(R.color.red),
                    focusedBorderColor = colorResource(R.color.red)
                )
            )

            if (searchText.value.isNotEmpty()) {
                IconButton(
                    modifier = Modifier.padding(top = 8.dp),
                    onClick = {
                        searchText.value = ""
                        keyboardController?.hide()
                    }
                ) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Очистить")
                }
            }
        }
    }

    @Composable
    fun TaxiCard(title: String, description: String) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = CardDefaults.cardColors(colorResource(R.color.red)),
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    color = colorResource(R.color.white0)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = colorResource(R.color.white)
                )
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun TaxiCardPager() {
        val taxiTypes = listOf(
            "Микроавтобус" to "Вместимость: до 1,5 тонн или 10-15 м³",
            "Газель" to "Вместимость: до 2 тонн или 16-20 м³",
            "Бортовая Газель" to "Вместимость: до 3 тонн или 20-25 м³",
            "Рефрижератор" to "Вместимость: до 1,5 тонн или 10-15 м³",
            "Грузовик (5-10 тонн)" to "Вместимость: до 10 тонн или 40-50 м³",
            "Фургон (до 20 тонн)" to "Вместимость: до 20 тонн или 80-100 м³"
        )
        val pagerState = rememberPagerState(pageCount = {
            taxiTypes.size
        })

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
            ) { page ->
                TaxiCard(
                    title = taxiTypes[page].first,
                    description = taxiTypes[page].second
                )
            }
        }
    }
}