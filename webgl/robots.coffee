gl = {}
triangleVerts = {}
triangleColors = {}
squareVerts = {}
squareColors = {}
shaderProgram = {}
pMatrix = mat4.create()
mvMatrix = mat4.create()

log = (message) ->
    alert(message)

initBuffer = (verts, itemSize, numItems) ->
    buff = gl.createBuffer()
    gl.bindBuffer(gl.ARRAY_BUFFER, buff)
    gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(verts), gl.STATIC_DRAW)
    buff.itemSize = itemSize
    buff.numItems = numItems
    buff

setMatrixUniforms = ->
    gl.uniformMatrix4fv(shaderProgram.pMatrixUniform, false, pMatrix)
    gl.uniformMatrix4fv(shaderProgram.mvMatrixUniform, false, mvMatrix)


drawBuffers = (verts, colors, mode) ->
    try
        gl.bindBuffer(gl.ARRAY_BUFFER, verts)
        gl.vertexAttribPointer(shaderProgram.vertexPositionAttribute, verts.itemSize, gl.FLOAT, false, 0, 0)
        gl.bindBuffer(gl.ARRAY_BUFFER, colors)
        gl.vertexAttribPointer(shaderProgram.vertexColorAttribute, colors.itemSize, gl.FLOAT, false, 0, 0)
        setMatrixUniforms()
        gl.drawArrays(mode, 0, verts.numItems)
    catch e
        log(e)


drawScene = ->
    gl.viewport(0, 0, gl.viewportWidth, gl.viewportHeight)
    gl.clear(gl.COLOR_BUFFER_BIT | gl.DEPTH_BUFFER_BIT)
    mat4.perspective(45, gl.viewportWidth / gl.viewportHeight, 0.1, 100.0, pMatrix)
    mat4.identity(mvMatrix)

    # triangle
    mat4.translate(mvMatrix, [-1.5, 0.0, -7.0])
    drawBuffers(triangleVerts, triangleColors, gl.TRIANGLES)

    # square
    mat4.translate(mvMatrix, [3.0, 0.0, 0.0])
    drawBuffers(squareVerts, squareColors, gl.TRIANGLE_STRIP)

initGL = (canvas) ->
    try
        gl = canvas.getContext("experimental-webgl")
        gl.viewportWidth = canvas.width
        gl.viewportHeight = canvas.height
    finally
        if gl is {}
            log("Can't init WebGL")

getShader = (id) ->
    shaderScript = document.getElementById(id)
    return null if not shaderScript

    str = ""
    k = shaderScript.firstChild
    while k
        if k.nodeType == 3
            str += k.textContent
        k = k.nextSibling

    shader = {}
    if shaderScript.type == "x-shader/x-fragment"
        shader = gl.createShader(gl.FRAGMENT_SHADER)
    else if shaderScript.type == "x-shader/x-vertex"
        shader = gl.createShader(gl.VERTEX_SHADER)
    else
        return null
    gl.shaderSource(shader, str)
    gl.compileShader(shader)

    if not gl.getShaderParameter(shader, gl.COMPILE_STATUS)
        log(gl.getShaderInfoLog(shader))
        return null
    shader


initShaders = ->
    fragmentShader = getShader("shader-fs")
    vertexShader = getShader("shader-vs")
    shaderProgram = gl.createProgram()
    gl.attachShader(shaderProgram, vertexShader)
    gl.attachShader(shaderProgram, fragmentShader)
    gl.linkProgram(shaderProgram)

    if not gl.getProgramParameter(shaderProgram, gl.LINK_STATUS)
        log("Could not initialize shaders")

    gl.useProgram(shaderProgram)

    shaderProgram.vertexPositionAttribute = gl.getAttribLocation(shaderProgram, "aVertexPosition")
    gl.enableVertexAttribArray(shaderProgram.vertexPositionAttribute)

    shaderProgram.vertexColorAttribute = gl.getAttribLocation(shaderProgram, "aVertexColor")
    gl.enableVertexAttribArray(shaderProgram.vertexColorAttribute)

    shaderProgram.pMatrixUniform = gl.getUniformLocation(shaderProgram, "uPMatrix")
    shaderProgram.mvMatrixUniform = gl.getUniformLocation(shaderProgram, "uMVMatrix")

init = ->
    canvas = document.getElementById('robot_canvas')
    initGL(canvas)
    initShaders()

    triangleVerts = initBuffer([
        0.0, 1.0, 0.0,
        -1.0, -1.0, 0.0,
        1.0, -1.0, 0.0
    ], 3, 3)
    triangleColors = initBuffer([
        1.0, 0.0, 0.0, 1.0,
        0.0, 1.0, 0.0, 1.0,
        0.0, 0.0, 1.0, 1.0
    ], 4, 3)

    squareVerts = initBuffer([
        1.0, 1.0, 0.0,
        -1.0, 1.0, 0.0,
        1.0, -1.0, 0.0,
        -1.0, -1.0, 0.0
    ], 3, 4)
    squareColors = initBuffer([
        1.0, 0.0, 0.0, 1.0,
        0.0, 1.0, 0.0, 1.0,
        0.0, 0.0, 1.0, 1.0,
        1.0, 1.0, 0.0, 0.5
    ], 4, 4)


    gl.clearColor(0,0,0,1)
    gl.enable(gl.DEPTH_TEST)

animate = () ->
    requestAnimationFrame(animate)
    drawScene()

init()
#animate()

drawScene()
