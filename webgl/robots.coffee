gl = {}
circles = {}
shaderProgram = {}
pMatrix = mat4.create()
mvMatrix = mat4.create()
mvMatrixStack = []
rTri = 0
rSquare = 0

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

setColor = (r,g,b) ->
    gl.uniform4f(shaderProgram.vColorUniform, r,g,b, 1)

drawBuffer = (verts, mode) ->
    try
        gl.bindBuffer(gl.ARRAY_BUFFER, verts)
        gl.vertexAttribPointer(shaderProgram.vertexPositionAttribute, verts.itemSize, gl.FLOAT, false, 0, 0)
        setMatrixUniforms()
        gl.drawArrays(mode, 0, verts.numItems)
    catch e
        log(e)

mvPushMatrix = ->
    copy = mat4.create()
    mat4.set(mvMatrix, copy)
    mvMatrixStack.push(copy)

mvPopMatrix = ->
    if mvMatrixStack.length == 0
        throw "Empty mvMatrixStack!"
    mvMatrix = mvMatrixStack.pop()

degToRad = (degrees) ->
    return degrees * Math.PI / 180

withRotation = (degs, f) ->
    mvPushMatrix()
    mat4.rotate(mvMatrix, degToRad(degs), [0, 1, 0])
    f()
    mvPopMatrix()


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

    shaderProgram.pMatrixUniform = gl.getUniformLocation(shaderProgram, "uPMatrix")
    shaderProgram.mvMatrixUniform = gl.getUniformLocation(shaderProgram, "uMVMatrix")
    shaderProgram.vColorUniform = gl.getUniformLocation(shaderProgram, "vColor")

R = 10.0
WAY_DENSITY = 200

initWay = (offset)->
    verts = ([(R+offset) * Math.cos(t), 0.0, (R+offset) * Math.sin(t)] for t in (
                   (Math.PI * 2 * i) / WAY_DENSITY for i in [0...WAY_DENSITY])).reduce(
                        (prev, curr, index, array) -> prev.concat(curr))
    initBuffer(verts, 3, verts.length / 3)

init = ->
    canvas = document.getElementById('robot_canvas')
    initGL(canvas)
    initShaders()

    circles = (initWay(offset) for offset in [-1.0, 1.0])

    gl.clearColor(0,0,0,1)
    gl.enable(gl.DEPTH_TEST)


drawScene = ->
    gl.viewport(0, 0, gl.viewportWidth, gl.viewportHeight)
    gl.clear(gl.COLOR_BUFFER_BIT | gl.DEPTH_BUFFER_BIT)
    mat4.perspective(45, gl.viewportWidth / gl.viewportHeight, 0.1, 100.0, pMatrix)
    mat4.identity(mvMatrix)

    mat4.translate(mvMatrix, [0.0,-5.0,-15.0])
    setColor(0,1,0)
    drawBuffer(c, gl.LINE_LOOP) for c in circles
    # triangle
    #mat4.translate(mvMatrix, [-1.5, 0.0, -7.0])
    #withRotation(rTri, -> drawBuffers(triangleVerts, triangleColors, gl.TRIANGLES))

    # square
    #mat4.translate(mvMatrix, [3.0, 0.0, 0.0])
    #withRotation(rSquare, -> drawBuffers(squareVerts, squareColors, gl.TRIANGLE_STRIP))

lastTime = 0
animate = ->
    timeNow = new Date().getTime()
    #rTri = (rTri + 1) % 360.0
    if lastTime != 0
        elapsed = timeNow - lastTime
        rTri += (90 * elapsed) / 1000.0
        rSquare += (75 * elapsed) / 1000.0
    lastTime = timeNow

tick = ->
    requestAnimationFrame(tick)
    drawScene()
    animate()

init()
#drawScene()
tick()
