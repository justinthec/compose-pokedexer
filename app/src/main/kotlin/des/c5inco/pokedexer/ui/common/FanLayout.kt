package des.c5inco.pokedexer.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.zIndex

@Composable
fun <T> FanLayout(
    modifier: Modifier = Modifier,
    items: List<T>,
    fanAngle: Float = 15f,
    itemContent: @Composable BoxScope.(T) -> Unit,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomCenter
    ) {
        val count = items.size
        val fanAngleStep = if (count > 1) fanAngle / (count - 1) else 0f

        items.forEachIndexed { index, item ->
            val rotation = if (count > 1) -fanAngle / 2 + index * fanAngleStep else 0f

            Box(
                modifier = Modifier
                    .graphicsLayer {
                        rotationZ = rotation
                        transformOrigin = TransformOrigin(0.5f, 1f)
                    }
                    .zIndex(index.toFloat())
            ) {
                itemContent(item)
            }
        }
    }
}
