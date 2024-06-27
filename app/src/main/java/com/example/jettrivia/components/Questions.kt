package com.example.jettrivia.components


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jettrivia.model.QuestionItem
import com.example.jettrivia.screens.QuestionViewModel
import com.example.jettrivia.util.AppColors

@Composable
fun Questions(viewModel: QuestionViewModel){
    val questions= viewModel.data.value.data?.toMutableList()

    val questionIndex = remember {
        mutableStateOf(0)
    }
    if (viewModel.data.value.loading == true){
        CircularProgressIndicator()

    }else {
        val question = try {
            questions?.get(questionIndex.value)
        }catch (ex:Exception){
            null
        }

        if (questions !=null){
            QuestionDisplay(question = question!!,questionIndex =questionIndex,
                viewModel = viewModel){
                questionIndex.value += 1
            }
        }
    }
}

//@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionDisplay(
    question :QuestionItem,
    questionIndex :MutableState<Int>,
    viewModel: QuestionViewModel,
    onNextClicked : (Int) -> Unit = {}
){
    val choicesState = remember(question) {
        question.choices.toMutableList()
    }
    val answerState = remember(question) {
        mutableStateOf<Int?>(null)
    }
    val correctAnswerState = remember(question){
        mutableStateOf<Boolean?>(null)
    }
    val updateAnswer : (Int)-> Unit = remember(question) {
        {
            answerState.value = it
            correctAnswerState.value = choicesState[it]==question.answer
        }
    }
    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f,15f),0f)

    Surface(modifier = Modifier
        .fillMaxHeight()
        .fillMaxWidth(),
        color = AppColors.mBlue){
        Column(modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start) {

            TopAppBar(title = {
                    Text(text = "QuizBot", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, color = AppColors.mBrown,
                        fontSize = 50.sp, textDecoration = TextDecoration.Underline)},
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                modifier = Modifier.padding(horizontal = 90.dp)
            )


            if (questionIndex.value >= 1){
                ShowProgress(score = questionIndex.value)
            }

            QuestionTracker(counter = questionIndex.value , viewModel.getTotalQuestionCount())
            DrawDottedLine(pathEffect = pathEffect)
            Column {
                Text(text = question.question,
                    modifier = Modifier
                        .padding(6.dp)
                        .align(alignment = Alignment.Start)
                        .fillMaxHeight(0.3f),
                    fontSize = 17.sp,
                    color = AppColors.mOffWhite,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 22.sp)
                //choices
                choicesState.forEachIndexed{index , answerText ->
                    Row(modifier = Modifier
                        .padding(3.dp)
                        .fillMaxWidth()
                        .height(45.dp)
                        .border(
                            width = 4.dp, brush = Brush.linearGradient(
                                colors = listOf(AppColors.mOffDarkPurple, AppColors.mOffDarkPurple)
                            ), shape = RoundedCornerShape(15.dp)
                        )
                        .clip(
                            RoundedCornerShape(
                                topStartPercent = 50,
                                topEndPercent = 50,
                                bottomEndPercent = 50,
                                bottomStartPercent = 50
                            )
                        )
                        .background(Color.Transparent),
                        verticalAlignment = Alignment.CenterVertically){
                        RadioButton(selected = (answerState.value == index),
                            onClick = {
                                updateAnswer(index)
                            },
                            modifier = Modifier.padding(16.dp),
                            colors = RadioButtonDefaults.colors(
                                selectedColor =
                                    if (correctAnswerState.value==true && index== answerState.value){
                                        Color.Green.copy(0.2f)
                                    }else{
                                        Color.Red.copy(0.2f)
                                    }))

                        val annotatedString = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Light,
                                color = if (correctAnswerState.value==true && index== answerState.value){
                                    Color.Green
                                }else if (correctAnswerState.value==false && index== answerState.value){
                                    Color.Red
                                }else{AppColors.mOffWhite}, fontSize = 17.sp
                            )){
                                append(answerText)
                            }
                        }
                        Text(text = annotatedString)


                    }
                }
            }
            Button(onClick = { onNextClicked(questionIndex.value) },
                modifier = Modifier
                    .padding(3.dp)
                    .align(alignment = Alignment.CenterHorizontally),
                shape = RoundedCornerShape(34.dp),
                colors = ButtonDefaults.buttonColors(AppColors.mLightBlue)) {
                    Text(
                        text = "Next",
                        color = AppColors.mOffWhite,
                        modifier = Modifier.padding(4.dp),
                        fontSize = 17.sp
                    )

                
            }


        }

    }
}

@Composable
fun DrawDottedLine(pathEffect: PathEffect) {
    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(1.dp)){
        drawLine(color = AppColors.mLightGray,
            start = Offset(0f,0f),
            end = Offset(size.width, y = 0f),
            pathEffect = pathEffect)

        
    }
}

@Preview
@Composable
fun ShowProgress(score: Int = 1){
    val gradient = Brush.linearGradient(listOf(Color(0xFFF95075),
        Color(0xFFBE6BE5)))

    val progressFactor by remember(score) {
        mutableStateOf(score*0.005f)

    }
    Row(modifier = Modifier
        .padding(3.dp)
        .fillMaxWidth()
        .height(45.dp)
        .border(
            width = 4.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    AppColors.mLightPurple,
                    AppColors.mLightPurple
                )
            ),
            shape = RoundedCornerShape(34.dp)
        )
        .clip(
            RoundedCornerShape(
                topStartPercent = 50,
                bottomEndPercent = 50,
                bottomStartPercent = 50,
                topEndPercent = 50
            )
        )
        .background(Color.Transparent),
        verticalAlignment = Alignment.CenterVertically) {
        Button(
            contentPadding = PaddingValues(0.1.dp),
            onClick = {},
            modifier = Modifier
                .fillMaxWidth(progressFactor)
                .background(brush = gradient),
            enabled = false,
            elevation = null
        ) {
            Text(text = (score * 10).toString(),
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(23.dp))
                    .fillMaxHeight(0.87f)
                    .fillMaxWidth()
                    .padding(6.dp),
                color = AppColors.mOffWhite,
                textAlign = TextAlign.Center)

            
        }


    }

}








@Preview
@Composable
fun QuestionTracker(counter : Int = 1,
                    outOff : Int = 100){
    Text(text = buildAnnotatedString {
        withStyle(style = ParagraphStyle(textIndent = TextIndent.None)){
            withStyle(style = SpanStyle(color = AppColors.mLightPurple,
                fontWeight = FontWeight.Bold,
                fontSize = 27.sp)){
                append("Question $counter /") }
            withStyle(style = SpanStyle(color = AppColors.mLightPurple,
                fontWeight = FontWeight.Light,
                fontSize = 14.sp)){
                append("$outOff")}
        }
    },
        modifier = Modifier.padding(20.dp))
}
