package developer.shivam.crescento

import android.graphics.Path

internal object PathProvider {

    fun getOutlinePath(width: Int, height: Int, curvatureHeight: Int, direction: Int, gravity: Int): Path {

        val mPath = Path()
        if (gravity == 0) {
            mPath.moveTo(0f, 0f)
            mPath.lineTo(0f, (height - curvatureHeight).toFloat())
            mPath.quadTo((width / 2).toFloat(), (height + curvatureHeight).toFloat(),
                width.toFloat(), (height - curvatureHeight).toFloat())
            mPath.lineTo(width.toFloat(), 0f)
            mPath.lineTo(0f, 0f)
            mPath.close()
        } else {
            mPath.moveTo(0f, height.toFloat())
            mPath.lineTo(0f, curvatureHeight.toFloat())
            mPath.quadTo((width / 2).toFloat(), (-curvatureHeight).toFloat(),
                width.toFloat(), curvatureHeight.toFloat())
            mPath.lineTo(width.toFloat(), height.toFloat())
            mPath.close()
        }

        return mPath
    }

    fun getClipPath(width: Int, height: Int, curvatureHeight: Int, direction: Int, gravity: Int): Path {

        val mPath = Path()
        if (gravity == 0) {

            mPath.moveTo(0f, (height - curvatureHeight).toFloat())
            mPath.quadTo((width / 2).toFloat(), (height + curvatureHeight).toFloat(),
                width.toFloat(), (height - curvatureHeight).toFloat())
            mPath.lineTo(width.toFloat(), 0f)
            mPath.lineTo(width.toFloat(), height.toFloat())
            mPath.lineTo(0f, height.toFloat())
            mPath.close()
        } else {
            mPath.moveTo(0f, 0f)
            mPath.lineTo(width.toFloat(), 0f)
            mPath.lineTo(width.toFloat(), curvatureHeight.toFloat())
            mPath.quadTo((width / 2).toFloat(), (-curvatureHeight).toFloat(),
                0f, curvatureHeight.toFloat())
            mPath.lineTo(0f, 0f)
            mPath.close()
        }

        return mPath
    }
}
